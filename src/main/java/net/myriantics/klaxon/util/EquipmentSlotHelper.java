package net.myriantics.klaxon.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;

public abstract class EquipmentSlotHelper {
    // theres probably a more efficient way of doing this but idc
    public static EquipmentSlot convert(InteractionHand playerHand) {
        return switch (playerHand) {
            case MAIN_HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
        };
    }

    public static InteractionHand getOppositeHand(InteractionHand hand) {
        return hand.equals(InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }
}
