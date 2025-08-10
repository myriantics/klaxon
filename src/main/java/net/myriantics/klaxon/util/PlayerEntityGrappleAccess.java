package net.myriantics.klaxon.util;

import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerEntityGrappleAccess {
    GrappleClawEntity klaxon$getGrappleClaw();

    void klaxon$setGrappleClaw(@Nullable GrappleClawEntity grappleClaw);

    boolean klaxon$isRetracting();

    void klaxon$setRetracting(boolean isRetracting);

    Vec3d klaxon$getFallbackGrappleClawPos();

    void klaxon$setFallbackGrappleClawPos(Vec3d grappleClawPos);
}
