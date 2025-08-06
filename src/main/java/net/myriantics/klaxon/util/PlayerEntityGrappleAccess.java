package net.myriantics.klaxon.util;

import net.myriantics.klaxon.entity.GrappleClawEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerEntityGrappleAccess {
    GrappleClawEntity klaxon$getGrappleClaw();

    void klaxon$setGrappleClaw(@Nullable GrappleClawEntity grappleClaw);
}
