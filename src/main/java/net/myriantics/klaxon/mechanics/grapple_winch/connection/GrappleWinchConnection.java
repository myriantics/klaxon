package net.myriantics.klaxon.mechanics.grapple_winch.connection;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import org.jetbrains.annotations.Nullable;

public abstract class GrappleWinchConnection {
    protected final int connectionId;

    protected boolean retracting = false;
    protected boolean hookAnchored = false;
    protected double cableLength = 64;
    protected double maxCableLength = 64;

    public GrappleWinchConnection(int connectionId) {
        this.connectionId = connectionId;
    }

    public void tick() {
        Vec3 compiledHookVec = Vec3.ZERO;
        Vec3 hookPos = this.getHookPos();
        Vec3 playerEyePos = this.getPlayerEyePos();

        @Nullable Player player = this.getPlayer();
        Vec3 hook2WielderVec = playerEyePos.subtract(hookPos);
        Vec3 normalizedHook2WielderVec = hook2WielderVec.normalize();
        double wielderDistance = hook2WielderVec.length();

        if (!this.hookAnchored && this.getHook() != null && this.getHook().klaxon$asEntity().isControlledByLocalInstance()) {

            // if we're not anchored, move the grappling hook
            if (!this.isHookAnchored()) {

                // retract grapple claw if owner pulls back before landing
                if (this.retracting && !this.getPlayer().isShiftKeyDown()) {
                    compiledHookVec = compiledHookVec.add(normalizedHook2WielderVec.scale(4f/20));
                }

                @Nullable Entity hookedEntity = this.getHook().klaxon$getHookedEntity();

                double activeCableLength = this.retracting && this.getPlayer().isShiftKeyDown()
                        ? this.getMaxCableLength()
                        : this.getCableLength();

                // make it so you're not stuck levitating with a vehicle
                // i'm gonna leave the funny bug in but at least make it less jank
                if (!this.retracting && player != null && hookedEntity != null && hookedEntity.equals(player.getVehicle())) {
                    double player2Vehicle = playerEyePos.distanceTo(hookedEntity.position());
                    if (player2Vehicle > wielderDistance) {
                        activeCableLength += hookedEntity.getEyeHeight() + 1;
                    }
                }

                // retract grapple claw if it hits limit
                if (wielderDistance >= activeCableLength) {

                    if (this.getHook().klaxon$asEntity().isControlledByLocalInstance() && wielderDistance >= maxCableLength * 1.2) {
                        this.getHook().klaxon$asEntity().push(this.getHook().klaxon$asEntity().getDeltaMovement().scale(-0.15));
                    }

                    compiledHookVec = compiledHookVec.add(normalizedHook2WielderVec.scale(4f/20));

                    if (this instanceof ServerGrappleWinchConnection connection) {
                        connection.tryPlayReboundSound();
                    }
                } else if (wielderDistance + 3 < activeCableLength * 0.90 && this instanceof ServerGrappleWinchConnection connection) {
                    // if we go back in bounds, we can play the rebound sound again
                    // this has a small deadzone because otherwise it would spam the shit out of the sound when dangling at the end of the cable.
                    connection.canPlayReboundSound = true;
                }
            }

            this.getHook().klaxon$asEntity().push(compiledHookVec);
        }
    }

    public int getId() {
        return this.connectionId;
    }

    public abstract int getPlayerId();

    public abstract int getHookId();

    public abstract Player getPlayer();

    public abstract GrapplingHook getHook();

    public abstract Vec3 getPlayerEyePos();

    public abstract Vec3 getHookPos();

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
