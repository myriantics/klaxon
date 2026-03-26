package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.util.BlockFaceRegion;
import org.jetbrains.annotations.Nullable;

public abstract class WrenchUtil {

    public static boolean isOverlayEnabling(ItemStack stack) {
        return stack.is(KlaxonItemTags.WRENCHABLE_INTERFACE_TRIGGERING_TOOLS) || stack.getItem() instanceof WrenchItem;
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
