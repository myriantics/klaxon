package net.myriantics.klaxon.item.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.recipes.hammer.HammerRecipe;
import net.myriantics.klaxon.util.AbilityModifierHelper;
import net.myriantics.klaxon.util.EquipmentSlotHelper;
import net.myriantics.klaxon.util.KlaxonTags;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.minecraft.block.FacingBlock.FACING;

public class HammerItem extends Item implements AttackBlockCallback, AttackEntityCallback {
    public static final float ATTACK_DAMAGE = 9.0F;
    public static final float ATTACK_SPEED = -2.8F;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public HammerItem(Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", ATTACK_DAMAGE, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", ATTACK_SPEED, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
        AttackBlockCallback.EVENT.register(this);
        AttackEntityCallback.EVENT.register(this);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && !state.isIn(BlockTags.FIRE)) {
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
    public boolean isSuitableFor(BlockState state) {
        return state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isIn(KlaxonTags.Blocks.HAMMER_MINEABLE)) {
            if (state.isIn(KlaxonTags.Blocks.HAMMER_INSTABREAK)) {
                // idk why you need the world and pos parameters for this the method doesnt even use them lmao
                return state.getHardness(null, null) * 30 * 5;
            } else {
                return 6.0F;
            }
        } else {
            return super.getMiningSpeedMultiplier(stack, state);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos interactionPos = context.getBlockPos();
        Direction hitDir = context.getSide();
        Position outputPos = getOutputLocation(hitDir, interactionPos);
        Vec3d outputVelocity = getOutputVelocity(hitDir);
        BlockState interactionState = context.getWorld().getBlockState(interactionPos);
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        Hand activeHand = context.getHand();

        if (player == null) {
            return ActionResult.PASS;
        }

        // hammering recipe
        if(player.isOnGround() && player.isSneaking()) {

            damageItem(player.getStackInHand(activeHand), player, player.getRandom(), true);
            world.addBlockBreakParticles(interactionPos, interactionState);

            if (interactionState.isIn(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT) && activeHand == Hand.MAIN_HAND) {
                RecipeType<HammerRecipe> type = HammerRecipe.Type.INSTANCE;
                SimpleInventory dummyInventory = new SimpleInventory(player.getOffHandStack());

                Optional<HammerRecipe> match = world.getRecipeManager().getFirstMatch(type, dummyInventory, world);
                if(match.isPresent()) {
                    if (!world.isClient) {
                        world.playSound(player, interactionPos, SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK, SoundCategory.PLAYERS, 2, 2f);
                        world.spawnEntity(new ItemEntity(world,
                                outputPos.getX(),
                                outputPos.getY(),
                                outputPos.getZ(),
                                match.get().getOutput(world.getRegistryManager()).copy(),
                                outputVelocity.x,
                                outputVelocity.y,
                                outputVelocity.z));
                    }

                    if (!player.isCreative()) {
                        player.getOffHandStack().decrement(1);
                    }
                }
            }

            if (world.isClient) {
                return ActionResult.SUCCESS;
            }

            updateObserversAndSuch(world, interactionPos, interactionState);

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    // block hit
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {

        BlockState targetBlockState = world.getBlockState(pos);

        if (player == null) {
            return ActionResult.PASS;
        }

        if (canWallJump(player)) {

            // may remove, idk thought itd be a good tradeoff for the power of the hammer
            // keeps wind charges relevant and encourages going up
            // may need to decrease attack cooldown or at least its impact on walljump power scaling
            // made it dynamic based on whether its an instabreak or not to account for dream shear leaf clutch edge case
            boolean processFallDamage = !targetBlockState.isIn(KlaxonTags.Blocks.HAMMER_INSTABREAK);

            processWallJumpPhysics(player, processFallDamage, 1.0f);

            if (player.getAttackCooldownProgress(0.5f) > 0.9) {
                world.addBlockBreakParticles(pos, targetBlockState);
                updateObserversAndSuch(world, pos, targetBlockState);
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

    // entity hit
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity targetEntity, @Nullable EntityHitResult hitResult) {
        if (player == null) {
            return ActionResult.PASS;
        }

        if (canWallJump(player)) {
            boolean wallJumpFallDamage = true;
            float wallJumpMultiplier = 1.0f;

            if (targetEntity.getType().isIn(KlaxonTags.Entities.BOUNCY_ENTITIES)) {
                wallJumpFallDamage = false;
                wallJumpMultiplier = 1.3f;
            }

            processWallJumpPhysics(player, wallJumpFallDamage, wallJumpMultiplier);

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
    private boolean canWallJump(PlayerEntity player) {
        return !player.isOnGround() && player.getMainHandStack().getItem() instanceof HammerItem && !player.isInsideWaterOrBubbleColumn();
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
        float n = 0.8F * player.getAttackCooldownProgress(0.5f) * AbilityModifierHelper.calculate(player) * multiplier;
        h *= n / m;
        k *= n / m;
        l *= n / m;

        if (fallDamage) {
            player.handleFallDamage(player.fallDistance, 1, player.getDamageSources().fall());
        }

        player.addVelocity(h, k, l);
    }

    private Position getOutputLocation(Direction direction, BlockPos pos) {
        Position centerPos = pos.toCenterPos();
        double x = centerPos.getX();
        double y = centerPos.getY();
        double z = centerPos.getZ();

        switch (direction) {
            case UP -> y += 0.60;
            case DOWN -> y -= 0.60;
            case NORTH -> z -= 0.60;
            case SOUTH -> z += 0.60;
            case EAST -> x += 0.60;
            case WEST -> x -= 0.60;
        }

        return new Vec3d(x, y, z);
    }

    private Vec3d getOutputVelocity(Direction direction) {
        double xVelo = 0.0;
        double yVelo = 0.0;
        double zVelo = 0.0;

        switch (direction) {
            case UP -> yVelo += 0.2;
            case DOWN -> yVelo -= 0.2;
            case NORTH -> zVelo -= 0.2;
            case SOUTH -> zVelo += 0.2;
            case EAST -> xVelo += 0.2;
            case WEST -> xVelo -= 0.2;
        }

        return new Vec3d(xVelo, yVelo, zVelo);
    }

    private void damageItem(ItemStack stack, LivingEntity attacker, Random random, boolean usedProperly) {
        int damageAmount;

        if (usedProperly) {
            damageAmount = random.nextBetween(0, 10) < 2 ? 1 : 0;
        } else {
            damageAmount = 1;
        }

        if (attacker instanceof PlayerEntity player && player.isCreative()) {
            damageAmount = 0;
        }

        stack.damage(damageAmount, attacker, (e) -> {
            e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
        });
    }

    private void updateObserversAndSuch(World world, BlockPos interactionPos, BlockState interactionState) {
        // block updating abilities
        // this quite literally allows you to hit something with a hammer to fix it
        world.updateNeighbor(interactionPos, interactionState.getBlock(), interactionPos);

        // trigger observers next to target block because its really funny
        for (Direction side : Direction.values()) {
            BlockPos observerPos = interactionPos.offset(side);
            BlockState observerState = world.getBlockState(observerPos);
            if (observerState.getBlock() instanceof ObserverBlock observerBlock) {
                if (observerPos.offset(observerState.get(FACING)).equals(interactionPos)) {
                    observerBlock.scheduledTick(observerState, (ServerWorld) world, observerPos, world.getRandom());
                }
            }
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }
}
