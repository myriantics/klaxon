package net.myriantics.klaxon.util.grapple_winch;

import net.myriantics.klaxon.entity.GrappleClawEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerEntityGrappleAccess {
    GrappleClawEntity klaxon$getGrappleClaw();

    void klaxon$setGrappleClaw(@Nullable GrappleClawEntity grappleClaw);

    boolean klaxon$isRetracting();

    void klaxon$setRetracting(boolean isRetracting);

    GrappleWinchConnectionData klaxon$getWinchFallbackData();

    void klaxon$setWinchConnectionData(GrappleWinchConnectionData winchFallbackData);

    boolean klaxon$hasActiveConnection();

    double klaxon$getCurrentWinchCableLength();

    void klaxon$setCurrentWinchCableLength(double spentWinchLength);

    void klaxon$resetWinchCableLength();
}
