package net.myriantics.klaxon.item.equipment.tools.grapple_winch;

import net.myriantics.klaxon.entity.GrappleClawEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerEntityGrappleAccess {
    default @Nullable GrappleClawEntity klaxon$getGrappleClaw() {
        return null;
    }

    void klaxon$setGrappleClaw(@Nullable GrappleClawEntity grappleClaw);

    default boolean klaxon$isRetracting() {
        return false;
    }

    default @Nullable GrappleWinchConnectionData klaxon$getWinchFallbackData() {
        return null;
    }

    default boolean klaxon$hasActiveConnection() {
        return false;
    }

    default double klaxon$getCurrentWinchCableLength() {
        return 0.0;
    }

    void klaxon$setCurrentWinchCableLength(double spentWinchLength);

    void klaxon$resetWinchCableLength();
}
