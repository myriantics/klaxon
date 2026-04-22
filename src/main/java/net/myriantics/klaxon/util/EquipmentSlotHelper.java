package net.myriantics.klaxon.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Objects;

public abstract class EquipmentSlotHelper {
    public static EquipmentSlot convert(InteractionHand playerHand) {
        if (Objects.requireNonNull(playerHand) == InteractionHand.OFF_HAND) {
            return EquipmentSlot.OFFHAND;
        } else {
            return EquipmentSlot.MAINHAND;
        }
    }

    public static InteractionHand getOppositeHand(InteractionHand hand) {
        return hand.equals(InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }
}
