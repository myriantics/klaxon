package net.myriantics.klaxon.item.tools;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mixin.ObserverBlockInvoker;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipe;
import net.myriantics.klaxon.util.AbilityModifierHelper;
import net.myriantics.klaxon.util.EquipmentSlotHelper;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.Optional;

import static net.minecraft.block.FacingBlock.FACING;

public class HammerItem extends Item implements AttackBlockCallback {
    public static final float ATTACK_DAMAGE = 9.0F;
    public static final float ATTACK_SPEED = -3.0F;

    public HammerItem(Settings settings) {
        super(settings);
        AttackBlockCallback.EVENT.register(this);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && !state.isIn(BlockTags.FIRE) && state.getHardness(world, pos) > 0) {
            damageItem(stack, miner, world.getRandom(), state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE));
        }
        return stack.isSuitableFor(state) || super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient) {
            damageItem(stack, attacker, attacker.getRandom(), true);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        return state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE)) {
            return 6.0F;
        } else {
            return super.getMiningSpeed(stack, state);
        }
    }

    // called in PlayerEntityMixin - supplemented by GameRendererMixin
    public static ActionResult useOnItemStack(ItemStack handStack, PlayerEntity player, ItemEntity targetDroppedItem, Hand activeHand) {
        World world = player.getWorld();

        Position outputPos = targetDroppedItem.getPos();

        // hammering recipe
        if(canProcessHammerRecipe(player)) {
            world.playSound(player, targetDroppedItem.getBlockPos(), SoundEvents.BLOCK_BASALT_BREAK, SoundCategory.PLAYERS, 2, 2f);
            damageItem(handStack, player, player.getRandom(), true);

            ItemStack targetStack = targetDroppedItem.getStack();

            // no need to process further if its the client
            if (world.isClient()) {
                // spawn le funny particle effects
                spawnHammeringParticleEffects(world, targetStack, 5, targetDroppedItem);
                return ActionResult.SUCCESS;
            }

            RecipeInput dummyInventory = new RecipeInput() {
                @Override
                public ItemStack getStackInSlot(int slot) {
                    return new SimpleInventory(targetStack).getStack(slot);
                }

                @Override
                public int getSize() {
                    return 1;
                }
            };

            Optional<RecipeEntry<HammeringRecipe>> match = world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.HAMMERING, dummyInventory, world);
            if(match.isPresent()) {
                if (!world.isClient) {
                   // spawn the dropped output item
                    ItemScatterer.spawn(
                            world,
                            outputPos.getX(),
                            outputPos.getY(),
                            outputPos.getZ(),
                            match.get().value().getResult(null));
                }

                // decrement target dropped item's stack because a match was present, so the item was used up in crafting
                targetDroppedItem.getStack().decrement(1);
            }

            return ActionResult.SUCCESS;

        }
        return ActionResult.PASS;
    }

    // block hit - used for walljumping
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {

        BlockState targetBlockState = world.getBlockState(pos);

        if (player == null) {
            return ActionResult.PASS;
        }

        float attackCooldownProgress = player.getAttackCooldownProgress(0.5f);

        if (canWallJump(player, targetBlockState) && attackCooldownProgress > 0.8) {

            // may remove, idk thought itd be a good tradeoff for the power of the hammer
            // keeps wind charges relevant and encourages going up
            // may need to decrease attack cooldown or at least its impact on walljump power scaling
            // if it's not an instabreak, process fall damage (instabreak = bbdelta < 1)
            boolean processFallDamage = targetBlockState.calcBlockBreakingDelta(player, null, null) < 1;

            world.addBlockBreakParticles(pos, targetBlockState);
            processWallJumpPhysics(player, processFallDamage, 1.0f);

            world.playSound(player, pos, SoundEvents.ENTITY_IRON_GOLEM_HURT, SoundCategory.PLAYERS, 2 * attackCooldownProgress, 2f * attackCooldownProgress);

            // update observers monitoring target block
            if (!world.isClient) {
                updateAdjacentMonitoringObservers(world, pos, targetBlockState);
            }

            player.onLanding();

            player.resetLastAttackedTicks();

            // damage it wheee
            if (!player.isCreative()) {
                damageItem(player.getStackInHand(hand), player, world.getRandom(), true);
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(KlaxonTags.Items.STEEL_INGOTS);
    }
    private boolean canWallJump(PlayerEntity player, BlockState state) {
        return !player.isOnGround()
                && player.getMainHandStack().getItem() instanceof HammerItem
                && !player.isInsideWaterOrBubbleColumn()
                // you can't walljump off of instabreakable blocks - in creative you can tho
                && (state.calcBlockBreakingDelta(player, null, null) < 1 || player.isCreative());
    }

    public static boolean canProcessHammerRecipe(PlayerEntity player) {
        return player.isOnGround() && player.isSneaking();
    }

    // so you can walljump in creative without demolishing your world
    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    // yoinked from trident riptide physics - edited to suit my needs
    private void processWallJumpPhysics(PlayerEntity player, boolean fallDamage, float multiplier) {
        float playerYaw = player.getYaw();
        float playerPitch = player.getPitch();
        float h = MathHelper.sin(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float k = MathHelper.sin(playerPitch * 0.017453292F);
        float l = -MathHelper.cos(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        float n = 0.6F * player.getAttackCooldownProgress(0.5f) * AbilityModifierHelper.calculate(player) * multiplier;
        h *= n / m;
        k *= n / m;
        l *= n / m;

        if (fallDamage) {
            player.handleFallDamage(player.fallDistance, 1, player.getDamageSources().fall());
        }

        player.addVelocity(h, k, l);
    }

    private static void damageItem(ItemStack stack, LivingEntity attacker, Random random, boolean usedProperly) {
        if (attacker.getWorld().isClient) {
            return;
        }

        int damageAmount;

        if (usedProperly) {
            damageAmount = random.nextBetween(0, 10) < 3 ? 1 : 0;
        } else {
            damageAmount = 1;
        }

        if (attacker instanceof PlayerEntity player && player.isCreative()) {
            damageAmount = 0;
        }

        stack.damage(damageAmount, attacker, EquipmentSlotHelper.convert(attacker.getActiveHand()));
    }

    private void updateAdjacentMonitoringObservers(World world, BlockPos interactionPos, BlockState interactionState) {
        // block updating abilities
        // this quite literally allows you to hit something with a hammer to fix it
        world.updateNeighbor(interactionPos, interactionState.getBlock(), interactionPos);

        // trigger observers next to target block because its really funny
        for (Direction side : Direction.values()) {
            BlockPos observerPos = interactionPos.offset(side);
            BlockState observerState = world.getBlockState(observerPos);
            if (observerState.getBlock() instanceof ObserverBlock observerBlock) {
                if (observerPos.offset(observerState.get(FACING)).equals(interactionPos)) {
                    ((ObserverBlockInvoker) observerBlock).invokeScheduledTick(observerState, (ServerWorld) world, observerPos, world.getRandom());
                }
            }
        }
    }

    // yoinked from living entity
    private static void spawnHammeringParticleEffects(World world, ItemStack stack, int count, Entity source) {
        Random random = source.getRandom();
        float pitch = source.getPitch();
        float yaw = source.getYaw();

        for (int i = 0; i < count; i++) {
            Vec3d vec3d = new Vec3d(((double)random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.rotateX(-pitch * (float) (Math.PI / 180.0));
            vec3d = vec3d.rotateY(-yaw * (float) (Math.PI / 180.0));
            double d = (double)(-random.nextFloat()) * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d(((double)random.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.rotateX(-pitch * (float) (Math.PI / 180.0));
            vec3d2 = vec3d2.rotateY(-yaw * (float) (Math.PI / 180.0));
            vec3d2 = vec3d2.add(source.getX(), source.getEyeY(), source.getZ());
            source.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }
    }
}
