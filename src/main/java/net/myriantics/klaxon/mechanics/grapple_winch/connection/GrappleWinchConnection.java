package net.myriantics.klaxon.mechanics.grapple_winch.connection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;

public abstract class GrappleWinchConnection {
    protected final int connectionId;

    protected boolean retracting = false;
    protected boolean hookAnchored = false;
    protected double cableLength = -1;
    protected double maxCableLength = -1;

    public GrappleWinchConnection(int connectionId) {
        this.connectionId = connectionId;
    }

    public void tick() {
        Vec3d compiledHookVec = Vec3d.ZERO;
        Vec3d hookPos = this.getHookPos();
        Vec3d playerEyePos = this.getPlayerEyePos();

        Vec3d normalizedHook2WielderVec = playerEyePos.subtract(hookPos).normalize();
        double wielderDistance = hookPos.distanceTo(playerEyePos);


        if (!this.hookAnchored && this.getHook() != null && this.getHook().klaxon$asEntity().isLogicalSideForUpdatingMovement()) {

            // if we're not anchored, move the grappling hook
            if (!this.isHookAnchored()) {

                // retract grapple claw if owner pulls back before landing
                if (this.retracting) {
                    compiledHookVec = compiledHookVec.add(normalizedHook2WielderVec.multiply(4f/20));
                }

                // retract grapple claw if it hits limit
                if (wielderDistance >= this.maxCableLength) {

                    if (this.getHook().klaxon$asEntity().isLogicalSideForUpdatingMovement() && wielderDistance >= maxCableLength * 1.2) {
                        this.getHook().klaxon$asEntity().addVelocity(this.getHook().klaxon$asEntity().getVelocity().multiply(-0.15));
                    }

                    compiledHookVec = compiledHookVec.add(normalizedHook2WielderVec.multiply(4f/20));

                    if (this instanceof ServerGrappleWinchConnection connection) {
                        connection.tryPlayReboundSound();
                    }
                } else if (wielderDistance < maxCableLength * 0.95 && this instanceof ServerGrappleWinchConnection connection) {
                    // if we go back in bounds, we can play the rebound sound again
                    // this has a small deadzone because otherwise it would spam the shit out of the sound when dangling at the end of the cable.
                    connection.canPlayReboundSound = true;
                }
            }

            this.getHook().klaxon$asEntity().addVelocity(compiledHookVec);
        }
    }

    public int getId() {
        return this.connectionId;
    }

    public abstract int getPlayerId();

    public abstract int getHookId();

    public abstract PlayerEntity getPlayer();

    public abstract GrapplingHook getHook();

    public abstract Vec3d getPlayerEyePos();

    public abstract Vec3d getHookPos();

    public boolean isHookAnchored() {
        return this.hookAnchored;
    }

    public double getMaxCableLength() {
        return this.maxCableLength;
    }

    public double getCableLength() {
        return this.cableLength;
    }

    public void setCableLength(double cableLength) {
        this.cableLength = Math.clamp(cableLength, 0, this.maxCableLength);
    }

    public abstract void resetCableLength();

    public boolean isRetracting() {
        return this.retracting;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GrappleWinchConnection connection && connection.connectionId == this.connectionId;
    }
}
