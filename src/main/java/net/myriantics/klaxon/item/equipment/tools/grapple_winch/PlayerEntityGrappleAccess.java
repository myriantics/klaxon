package net.myriantics.klaxon.item.equipment.tools.grapple_winch;

import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerEntityGrappleAccess {
    @Nullable GrappleClawEntity klaxon$getGrappleClaw();

    void klaxon$setGrappleClaw(@Nullable GrappleClawEntity grappleClaw);

    boolean klaxon$isRetracting();

    @Nullable GrappleWinchConnectionData klaxon$getConnectionData();

    void klaxon$setConnectionData(@Nullable GrappleWinchConnectionData connectionData);

    boolean klaxon$hasActiveConnection();

    double klaxon$getCurrentWinchCableLength();

    void klaxon$setCurrentWinchCableLength(double spentWinchLength);

    void klaxon$resetWinchCableLength();
}
