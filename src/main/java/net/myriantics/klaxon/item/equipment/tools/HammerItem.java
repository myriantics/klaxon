package net.myriantics.klaxon.item.equipment.tools;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.util.EntityWeightHelper;
import net.myriantics.klaxon.util.PermissionsHelper;
import net.myriantics.klaxon.registry.*;
import net.myriantics.klaxon.mixin.ObserverBlockInvoker;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipe;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.AbilityModifierCalculator;
import net.myriantics.klaxon.util.EquipmentSlotHelper;

import java.util.List;
import java.util.Optional;

import static net.minecraft.block.FacingBlock.FACING;

public class HammerItem extends ToolItem {
    public static final float STEEL_HAMMER_BASE_ATTACK_DAMAGE = 5.0F;
    public static final float STEEL_HAMMER_ATTACK_SPEED = -3.1F;

    public HammerItem(Settings settings) {
        super(KlaxonToolMaterials.STEEL, settings.maxCount(1));
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, material.getAttackDamage() + baseAttackDamage, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                ).build();
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        // only damage the item if it's not tall grass or something that shouldn't reduce durability
        if (!world.isClient && state.getHardness(world, pos) > 0) {
            damageItem(stack, miner, state.isIn(KlaxonBlockTags.HAMMER_MINEABLE));
        }
        return stack.isSuitableFor(state) || super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getWorld();
        if (!world.isClient) {
            damageItem(stack, attacker, true);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        return state.isIn(KlaxonBlockTags.HAMMER_MINEABLE);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isIn(KlaxonBlockTags.HAMMER_MINEABLE)) {
            return 6.0F;
        } else {
            return super.getMiningSpeed(stack, state);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        Vec3d clickedPos = context.getHitPos();
        ItemStack handStack = context.getStack();

        // check if player is actually in the position to hammer stuff before doing anything
        if (player != null && canProcessHammerRecipe(player)) {

            List<ItemEntity> selectedItems = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), Box.of(clickedPos, 0.5, 0.5, 0.5), (e) -> true);

            // if there aren't any dropped items in the targeted area, don't do anything
            if (selectedItems.isEmpty()) {
                return ActionResult.PASS;
            }

            // play sound and damage item only after we're sure there are items selected
            world.playSound(player, BlockPos.ofFloored(clickedPos), SoundEvents.BLOCK_BASALT_BREAK, SoundCategory.PLAYERS, 2, 2f);
            damageItem(handStack, player, true);

            boolean recipeSuccessPresent = false;

            // run recipe and dropping code for each selected dropped item
            for (ItemEntity iteratedDroppedItem : selectedItems) {

                ItemStack targetStack = iteratedDroppedItem.getStack().copy();
                Position outputPos = iteratedDroppedItem.getPos();

                // dont run recipe stuff on the client
                if (!world.isClient()) {

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
                        // indicate that at least one craft was successful
                        recipeSuccessPresent = true;

                        // spawn the dropped output item
                        ItemScatterer.spawn(
                                world,
                                outputPos.getX(),
                                outputPos.getY(),
                                outputPos.getZ(),
                                match.get().value().craft(dummyInventory, world.getRegistryManager()));

                        // decrement target dropped item's stack because a match was present, so the item was used up in crafting
                        targetStack.decrement(1);
                        if (targetStack.getCount() == 0) {
                            iteratedDroppedItem.remove(Entity.RemovalReason.DISCARDED);
                        } else {
                            iteratedDroppedItem.setStack(targetStack);
                        }
                    }

                } else {
                    // spawn hammering particle effects
                    spawnHammeringParticleEffects(world, targetStack, 5, iteratedDroppedItem);
                }
            }

            if (!world.isClient()) {
                // pop the hammering advancement
                KlaxonAdvancementTriggers.triggerHammerUse((ServerPlayerEntity) player, UsageType.RECIPE_SUCCESS);
                // trip sculk sensors
                world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, clickedPos);
            }

            return ActionResult.SUCCESS;

        }

        return ActionResult.PASS;
    }

    // rising edge block hit - uses MinecraftClientMixin and HammerWalljumpTriggerPacket
    // called on both client and server
    public static void processHammerWalljump(PlayerEntity player, World world, BlockPos pos, Direction direction) {

        BlockState targetBlockState = world.getBlockState(pos);

        if (player == null) {
            KlaxonCommon.LOGGER.error("Player is null when trying to do a Hammer Walljump. Why?");
            return;
        }

        float attackCooldownProgress = player.getAttackCooldownProgress(0.5f);

        if (canWallJump(player, targetBlockState) && attackCooldownProgress > 0.8) {

            // may remove, idk thought itd be a good tradeoff for the power of the hammer
            // keeps wind charges relevant and encourages going up
            // may need to decrease attack cooldown or at least its impact on walljump power scaling
            // if it's not an instabreak, process fall damage (instabreak = bbdelta < 1)
            boolean processFallDamage = targetBlockState.calcBlockBreakingDelta(player, null, null) < 1;

            world.addBlockBreakParticles(pos, targetBlockState);
            boolean walljumpSucceeded = processWallJumpPhysics(player, processFallDamage, 1.0f);

            world.playSound(player, pos, SoundEvents.ENTITY_IRON_GOLEM_HURT, SoundCategory.PLAYERS, 2 * attackCooldownProgress, 2f * attackCooldownProgress);

            if (!world.isClient()) {
                // update observers monitoring target block - doesn't work in adventure
                if (PermissionsHelper.canModifyWorld(player)) {
                    updateAdjacentMonitoringObservers(world, pos, targetBlockState);
                }

                KlaxonAdvancementTriggers.triggerHammerUse((ServerPlayerEntity) player, walljumpSucceeded ? UsageType.WALLJUMP_SUCCEEDED : UsageType.WALLJUMP_FAILED);
                // if player overpowered steel armor with strength proc this
                if (walljumpSucceeded && EntityWeightHelper.isHeavy(player)) {
                    KlaxonAdvancementTriggers.triggerHammerUse((ServerPlayerEntity) player, UsageType.STRENGTH_WALLJUMP_SUCCEEDED);
                }
            }

            // trip sculk sensors
            world.emitGameEvent(player, GameEvent.HIT_GROUND, pos);

            player.onLanding();

            player.resetLastAttackedTicks();

            // damage it wheee
            damageItem(player.getMainHandStack(), player, true);
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public static boolean canWallJump(PlayerEntity player, BlockState state) {
        // originally you could use the hammer in spectator - funny, but not good.
        return !player.isSpectator()
                // prevents spammy bs when descending and unintentional hammer walljump procs
                && player.getVelocity().getY() > 0
                // make sure they're actually holding a hammer
                && player.getMainHandStack().isOf(KlaxonItems.STEEL_HAMMER)
                // allows players to not walljump if they don't want to
                && !player.isSneaking()
                // you cant walljump when you're in a boat or some this
                && player.getVehicle() == null
                // walljumping in water is janky
                && !player.isInFluid()
                // you can't walljump off of instabreakable blocks - in creative you can tho - also in adventure
                && (state.calcBlockBreakingDelta(player, null, null) < 1 || player.isCreative() || !player.getAbilities().allowModifyWorld);
    }

    public static boolean canProcessHammerRecipe(PlayerEntity player) {
        // has conditions so that player has control - as well as item hitboxes not blocking block selection constantly
        return player.isOnGround() && player.isSneaking();
    }

    // so you can walljump in creative without demolishing your world
    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    // yoinked from trident riptide physics - edited to suit my needs
    private static boolean processWallJumpPhysics(PlayerEntity player, boolean fallDamage, float multiplier) {
        float playerYaw = player.getYaw();
        float playerPitch = player.getPitch();
        float h = MathHelper.sin(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float k = MathHelper.sin(playerPitch * 0.017453292F);
        float l = -MathHelper.cos(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        float n = 0.6F * player.getAttackCooldownProgress(0.5f) * AbilityModifierCalculator.calculateHammerWalljumpMultiplier(player) * multiplier;
        h *= n / m;
        k *= n / m;
        l *= n / m;

        if (fallDamage) {
            player.handleFallDamage(player.fallDistance, 1, player.getDamageSources().fall());
        }

        player.addVelocity(h, k, l);

        // returns false if failed, true if succeeded in moving player
        return n > 0;
    }

    private static void damageItem(ItemStack stack, LivingEntity attacker, boolean usedProperly) {
        if (attacker.getWorld().isClient) {
            return;
        }

        stack.damage(usedProperly ? 1 : 2, attacker, EquipmentSlotHelper.convert(attacker.getActiveHand()));
    }

    private static void updateAdjacentMonitoringObservers(World world, BlockPos interactionPos, BlockState interactionState) {
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

    // called in PlayerEntityMixin and MobEntityMixin
    public static DamageSource getDamageType(Entity attacker, boolean willCrit) {
        return willCrit ? KlaxonDamageTypes.hammerWalloping(attacker) : KlaxonDamageTypes.hammerBonking(attacker);
    }

    public enum UsageType implements StringIdentifiable {
        RECIPE_SUCCESS,
        WALLJUMP_SUCCEEDED,
        STRENGTH_WALLJUMP_SUCCEEDED,
        WALLJUMP_FAILED;

        private static final Codec<UsageType> CODEC = StringIdentifiable.createCodec(UsageType::values);

        @Override
        public String asString() {
            return this.toString().toLowerCase();
        }

        public static Codec<UsageType> getCodec() {
            return CODEC;
        }
    }
}
