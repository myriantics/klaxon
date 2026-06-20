package net.myriantics.klaxon.mechanics.logistics.itemduct.network;

import net.minecraft.core.Direction;

public interface DuctThrustProvider {
    Direction getThrustDirection();

    float getThrustVelocity();
}
