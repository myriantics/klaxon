package net.myriantics.klaxon.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Hand;

public abstract class EquipmentSlotHelper {
    // theres probably a more efficient way of doing this but idc
    public static EquipmentSlot convert(Hand playerHand) {
        switch (playerHand) {
            case MAIN_HAND -> {
                return EquipmentSlot.MAINHAND;
            }
            case OFF_HAND -> {
                return EquipmentSlot.OFFHAND;
            }
        }
    return EquipmentSlot.MAINHAND;
    }

    public static Hand getOppositeHand(Hand hand) {
        return hand.equals(Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }
}
