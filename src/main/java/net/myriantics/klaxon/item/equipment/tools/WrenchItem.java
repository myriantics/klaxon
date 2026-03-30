package net.myriantics.klaxon.item.equipment.tools;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.mechanics.wrench.*;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class WrenchItem extends DiggerItem {
    public WrenchItem(Tier material, Properties settings) {
        super(material, KlaxonBlockTags.WRENCH_MINEABLE, settings
                .component(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT.value(), new InstabreakingToolComponent(KlaxonBlockTags.WRENCH_INSTABREAKABLE))
        );
    }

    public static ItemAttributeModifiers createAttributes(Tier material, float baseAttackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, material.getAttackDamageBonus() + baseAttackDamage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                ).build();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos targetPos = context.getClickedPos();
        BlockState targetState = level.getBlockState(targetPos);
        Player player = context.getPlayer();
        ItemStack wrenchStack = context.getItemInHand();

        if (player == null) {
            return super.useOn(context);
        }

        // Wrench pickup ability requires both CAN_PLACE_ON and CAN_DESTROY components to work in Adventure Mode
        if (canPickup(targetState, targetPos, level, player, wrenchStack)) {
            if (level instanceof ServerLevel serverWorld) {
                List<ItemStack> outputStacks = Block.getDrops(targetState, serverWorld, targetPos, serverWorld.getBlockEntity(targetPos));
                if (!outputStacks.isEmpty()) {
                    for (ItemStack stack : outputStacks) {
                        // don't insert the stack if player is already creative - unless it's valuable, then do
                        if (!stack.isEmpty() && (!player.isCreative() || stack.has(DataComponents.CONTAINER) || stack.has(DataComponents.CONTAINER_LOOT))) {
                            // dump the rest of the stack into the level if it doesn't fit into player's inventory
                            if (!player.getInventory().add(stack)) {
                                if (!stack.isEmpty()) {
                                    Block.popResource(serverWorld, targetPos, stack);
                                }
                            }
                        }
                    }
                }

                // drop is false here because we already handled the drops
                // only break on server because sound plays twice on client otherwise
                level.destroyBlock(targetPos, false, player);
                KlaxonAdvancementTriggers.triggerWrenchUsage((ServerPlayer) player, UsageType.PICKUP, targetState);
            }

            return InteractionResult.SUCCESS;
        }

        // Only requires CAN_PLACE_ON in adventure mode
        if (allowDefaultRotationBehavior(context.getLevel().registryAccess(), targetState)) {
            WrenchActionContext.Manual manual = new WrenchActionContext.Manual(level, targetState, targetPos, wrenchStack, player, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside()), context.getHand());


            Optional<InteractionResult> result = Optional.empty();

            // apply the first valid behavior to target state
            for (BlockStateWrenchBehavior<? extends Comparable<?>> behavior : KlaxonRegistries.BLOCK_STATE_WRENCH_BEHAVIORS) {
                if (behavior.test(targetState)) {
                    WrenchInteractionMap map = behavior.getManualInteractionMap(manual);
                    float clickedX = manual.getGuiOrientation().getClickedX();
                    float clickedY = manual.getGuiOrientation().getClickedY();
                    result = map.select(clickedX, clickedY).handle(manual, map.getRotation(targetState, manual.getGuiOrientation()));
                    if (result.isPresent()) {
                        break;
                    }
                }
            }

            if (result.isPresent()) {
                Vec3 cords = targetPos.getCenter();
                level.playLocalSound(cords.x(), cords.y(), cords.z(), targetState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 0.7f + 0.3f * level.getRandom().nextFloat(), 1.0f, true);
                if (player instanceof ServerPlayer serverPlayer) {
                    KlaxonAdvancementTriggers.triggerWrenchUsage(serverPlayer, UsageType.ROTATION, targetState);
                }
                return result.get();
            }
        }

        return InteractionResult.FAIL;
    }

    public static boolean canRotate(RegistryAccess manager, BlockState targetState) {
        // blocks in the deny list cannot be rotated
        if (targetState.is(KlaxonBlockTags.WRENCH_INTERACTION_GENERAL_DENYLIST)) {
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

    public static boolean allowDefaultRotationBehavior(RegistryAccess manager, BlockState targetState) {
        return !(targetState.getBlock() instanceof Wrenchable) && canRotate(manager, targetState);
    }

    public boolean canPickup(BlockState targetState, BlockPos targetPos, Level world, @Nullable Player player, ItemStack wrenchStack) {
        // if the state is in the denylist, fail pickup
        if (targetState.is(KlaxonBlockTags.WRENCH_PICKUP_DENYLIST)) {
            return false;
        }

        // if we have a player and it's not sneaking, fail pickup
        if (player != null && !player.isShiftKeyDown()) {
            return false;
        }

        // if the state isn't in the allowlist, fail pickup
        if (!targetState.is(KlaxonBlockTags.WRENCH_PICKUP_ALLOWLIST)) {
            return false;
        }

        return player == null || PermissionsHelper.canModifyWorld(player) || wrenchStack.canBreakBlockInAdventureMode(new BlockInWorld(world, targetPos, false));
    }

    public enum UsageType implements StringRepresentable {
        ROTATION,
        PICKUP;

        public static Codec<UsageType> CODEC = StringRepresentable.fromEnum(UsageType::values);

        @Override
        public String getSerializedName() {
            return toString().toLowerCase();
        }
    }
}
