package net.myriantics.klaxon.mechanics.logistics.itemduct.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class DuctJunction {

    private final Map<BlockPos, DuctThrustProvider> thrustProviders = new HashMap<>();

    private Direction preferredDirection = Direction.NORTH;
    private float velocity = 0;

    public float updateVelocity() {
        float xVelocity = 0;
        float yVelocity = 0;
        float zVelocity = 0;
        for (DuctThrustProvider provider : this.thrustProviders.values()) {
            Direction direction = provider.getThrustDirection();
            switch (direction.getAxis()) {
                case X -> xVelocity += direction.getStepX() * provider.getThrustVelocity();
                case Y -> yVelocity += direction.getStepY() * provider.getThrustVelocity();
                case Z -> zVelocity += direction.getStepZ() * provider.getThrustVelocity();
            }
        }

        this.preferredDirection = Direction.getNearest(xVelocity, yVelocity, zVelocity);
        Direction.orderedByNearest()
    }

    public float getVelocity() {

    }
}
