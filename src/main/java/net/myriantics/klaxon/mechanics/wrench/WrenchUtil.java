package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.util.BlockFaceRegion;
import org.jetbrains.annotations.Nullable;

public abstract class WrenchUtil {

    // private static final WrenchInteractionMap PICKUP = WrenchInteractionMap.fullBlock(WrenchInteraction.of(Wren))

    private static final WrenchInteractionMap EMPTY = WrenchInteractionMap.create();

    public static boolean isOverlayEnabling(ItemStack stack) {
        return stack.is(KlaxonItemTags.WRENCHABLE_INTERFACE_TRIGGERING_TOOLS) || stack.getItem() instanceof WrenchItem;
    }

    public static WrenchInteractionMap getInteractionMap(WrenchActionContext.Manual manual) {
        BlockState targetState = manual.getTargetState();
        for (BlockStateWrenchBehavior<? extends Comparable<?>> behavior : KlaxonRegistries.BLOCK_STATE_WRENCH_BEHAVIORS) {
            if (behavior.test(targetState)) {
                return behavior.getManualInteractionMap(manual);
            }
        }
        return EMPTY;
    }

    public static WrenchActionType getActionType(WrenchActionContext.Manual manual) {
        Level level = manual.level();
        Player player = manual.getPlayer();
        ItemStack wrenchStack = manual.getWrenchStack();
        BlockPos targetPos = manual.getTargetPos();
        BlockState targetState = manual.getTargetState();
        if (wrenchStack.getItem() instanceof WrenchItem wrenchItem && wrenchItem.canPickup(targetState, targetPos, level, player, wrenchStack)) {
            return KlaxonWrenchActionTypes.PICKUP.value();
        }

        return KlaxonWrenchActionTypes.PASS.value();
    }

    public static @Nullable InteractionHand selectWrenchHand(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (isOverlayEnabling(stack)) {
            return InteractionHand.MAIN_HAND;
        }
        stack = player.getOffhandItem();
        if (isOverlayEnabling(stack)) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    public static BlockFaceRegion getRegionForContext(WrenchActionContext.Manual manual) {
        return BlockFaceRegion.FULL_BLOCK;
    }
}
