package net.myriantics.klaxon.item.equipment.tools;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.wrench.WrenchInteractionDenialPredicate;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WrenchItem extends MiningToolItem {
    public WrenchItem(ToolMaterial material, Settings settings) {
        super(material, KlaxonBlockTags.WRENCH_MINEABLE, settings
                .component(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT, new InstabreakingToolComponent(KlaxonBlockTags.WRENCH_INSTABREAKABLE))
        );
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
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos targetPos = context.getBlockPos();
        BlockState targetState = world.getBlockState(targetPos);
        PlayerEntity player = context.getPlayer();
        ItemStack wrenchStack = context.getStack();

        if (player == null) {
            return super.useOnBlock(context);
        }

        // Wrench pickup ability requires both CAN_PLACE_ON and CAN_DESTROY components to work in Adventure Mode
        if (canPickup(targetState, targetPos, world, player, wrenchStack)) {
            if (world instanceof ServerWorld serverWorld) {
                List<ItemStack> outputStacks = Block.getDroppedStacks(targetState, serverWorld, targetPos, serverWorld.getBlockEntity(targetPos));
                if (!outputStacks.isEmpty()) {
                    for (ItemStack stack : outputStacks) {
                        // don't insert the stack if player is already creative - unless it's valuable, then do
                        if (!stack.isEmpty() && (!player.isCreative() || stack.contains(DataComponentTypes.CONTAINER) || stack.contains(DataComponentTypes.CONTAINER_LOOT))) {
                            // dump the rest of the stack into the world if it doesn't fit into player's inventory
                            if (context.getHand().equals(Hand.MAIN_HAND) && (player.getOffHandStack().isEmpty() || KlaxonItemStackHelper.canStacksMerge(player.getOffHandStack(), stack))) {
                                player.setStackInHand(Hand.OFF_HAND, KlaxonItemStackHelper.combineStacksIfPossible(stack, player.getOffHandStack()));
                            } else if (!player.getInventory().insertStack(stack)) {
                                if (!stack.isEmpty()) {
                                    Block.dropStack(serverWorld, targetPos, stack);
                                }
                            }
                        }
                    }
                }

                // drop is false here because we already handled the drops
                // only break on server because sound plays twice on client otherwise
                world.breakBlock(targetPos, false, player);
                KlaxonAdvancementTriggers.triggerWrenchUsage((ServerPlayerEntity) player, UsageType.PICKUP, targetState);
            }

            return ActionResult.SUCCESS;
        }

        // Only requires CAN_PLACE_ON in adventure mode
        if (allowDefaultRotationBehavior(context.getWorld().getRegistryManager(), targetState)) {
            ManualWrenchInteractionContext wrenchContext = new ManualWrenchInteractionContext(targetState, wrenchStack, world, player, context.getHand(), new BlockHitResult(context.getHitPos(), context.getSide(), context.getBlockPos(), context.hitsInsideBlock()));

            BlockState newState = targetState;

            // apply all valid behaviors to new state
            for (BlockStateWrenchBehavior<? extends Comparable<?>> behavior : KlaxonRegistries.BLOCK_STATE_WRENCH_BEHAVIORS) {
                if (behavior.test(newState)) {
                    newState = behavior.applyManual(newState, wrenchContext);
                }
            }

            if (newState != targetState) {
                Vec3d cords = targetPos.toCenterPos();
                world.playSound(cords.getX(), cords.getY(), cords.getZ(), targetState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 0.7f + 0.3f * world.getRandom().nextFloat(), 1.0f, true);
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    KlaxonAdvancementTriggers.triggerWrenchUsage(serverPlayer, UsageType.ROTATION, targetState);
                    world.setBlockState(targetPos, newState);
                    world.updateNeighbor(targetPos, newState.getBlock(), targetPos);
                    world.updateComparators(targetPos, newState.getBlock());
                }
                return ActionResult.SUCCESS;
            }
        }

        return super.useOnBlock(context);
    }

    public static boolean canRotate(DynamicRegistryManager manager, BlockState targetState) {
        // blocks in the deny list cannot be rotated
        if (targetState.isIn(KlaxonBlockTags.WRENCH_INTERACTION_GENERAL_DENYLIST)) {
            return false;
        }

        if (WrenchInteractionDenialPredicate.wrenchInteractionBlocked(manager, targetState)) {
            return false;
        }

        if (targetState.getBlock() instanceof Wrenchable) {
            return true;
        }

        for (BlockStateWrenchBehavior<? extends Comparable<?>> behavior : KlaxonRegistries.BLOCK_STATE_WRENCH_BEHAVIORS) {
            if (behavior.test(targetState)) {
                return true;
            }
        }

        return false;
    }

    public static boolean allowDefaultRotationBehavior(DynamicRegistryManager manager, BlockState targetState) {
        return !(targetState.getBlock() instanceof Wrenchable) && canRotate(manager, targetState);
    }

    public static boolean canPickup(BlockState targetState, BlockPos targetPos, World world, @Nullable PlayerEntity player, ItemStack wrenchStack) {
        // if the state is in the denylist, fail pickup
        if (targetState.isIn(KlaxonBlockTags.WRENCH_PICKUP_DENYLIST)) {
            return false;
        }

        // if we have a player and it's not sneaking, fail pickup
        if (player != null && !player.isSneaking()) {
            return false;
        }

        // if the state isn't in the allowlist, fail pickup
        if (!targetState.isIn(KlaxonBlockTags.WRENCH_PICKUP_ALLOWLIST)) {
            return false;
        }

        return player == null || PermissionsHelper.canModifyWorld(player) || wrenchStack.canBreak(new CachedBlockPosition(world, targetPos, false));
    }

    public enum UsageType implements StringIdentifiable {
        ROTATION,
        PICKUP;

        public static Codec<UsageType> CODEC = StringIdentifiable.createCodec(UsageType::values);

        @Override
        public String asString() {
            return toString().toLowerCase();
        }
    }
}
