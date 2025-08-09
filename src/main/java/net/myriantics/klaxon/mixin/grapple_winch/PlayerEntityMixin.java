package net.myriantics.klaxon.mixin.grapple_winch;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.util.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityGrappleAccess {

    @Unique
    private GrappleClawEntity klaxon$grappleClaw = null;

    @Unique
    private Vec3d klaxon$grappleClawPos = null;

    @Unique
    private boolean klaxon$isRetractingGrappleWinch = false;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public GrappleClawEntity klaxon$getGrappleClaw() {
        return klaxon$grappleClaw;
    }

    @Override
    public void klaxon$setGrappleClaw(GrappleClawEntity grappleClaw) {
        this.klaxon$grappleClaw = grappleClaw;
    }

    @Override
    public boolean klaxon$isRetracting() {
        return klaxon$isRetractingGrappleWinch;
    }

    @Override
    public void klaxon$setRetracting(boolean isRetracting) {
        this.klaxon$isRetractingGrappleWinch = isRetracting;
    }

    @Override
    public Vec3d klaxon$getGrappleClawPos() {
        return klaxon$grappleClawPos;
    }

    @Override
    public void klaxon$setGrappleClawPos(Vec3d grappleClawPos) {
        this.klaxon$grappleClawPos = grappleClawPos;
    }

    @Inject(
            method = "tickMovement",
            at = @At(value = "HEAD")
    )
    private void klaxon$tickGrappleWinchMovement(CallbackInfo ci) {
        // only run player movement logic when we have an active anchored grapple claw
        if (klaxon$grappleClaw != null) {
            Vec3d selfVec = Vec3d.ZERO;

            // make sure grapple claw is loaded and anchored
            if (klaxon$grappleClawPos != null && klaxon$grappleClaw.isAnchored()) {
                // get limits and data from claw
                double clawDistance = getPos().squaredDistanceTo(klaxon$grappleClawPos);
                double targetRangeSquared = klaxon$grappleClaw.getTargetRangeSquared();
                double maxRangeSquared = GrappleClawEntity.MAX_RANGE_SQUARED;

                // get movement vectors
                Vec3d playerToClawVec = klaxon$grappleClaw.getPos().subtract(this.getPos()).normalize();
                Vec3d playerFacingVec = this.getRotationVec(1.0f).normalize();

                // tick retraction movement
                if (klaxon$isRetractingGrappleWinch) {

                    // transform movement vectors
                    Vec3d playerToClawRetractionVec = playerToClawVec.multiply(2./20);
                    // player can direct movement with facing direction to combat getting stuck under ledges
                    Vec3d playerFacingRetractionVec = playerFacingVec.multiply(1.5/20).multiply(this.isSprinting() ? 2 : 1);

                    // add vectors to self vector
                    // owner goes towards claw if not sneaking, away if they are sneaking
                    if (!this.isSneaking()) {
                        selfVec = selfVec.add(playerToClawRetractionVec).add(playerFacingRetractionVec);
                    } else if (clawDistance < targetRangeSquared && playerToClawRetractionVec.getY() >= 0) {
                        selfVec = selfVec.add(0, -playerToClawVec.getY(), 0).multiply(0.5).add(playerFacingVec.negate().multiply(0.3, 0.1, 0.3));
                    }
                }

                // apply velocity to player if they go past target range
                // retraction is only capped at the max range
                // also this is a dope ass spot to use ternary operators omg
                if (clawDistance >= (klaxon$isRetractingGrappleWinch ? maxRangeSquared : targetRangeSquared)) {
                    Vec3d playerRangeCorrectionVec = playerToClawVec.multiply(0.1);
                    playerRangeCorrectionVec = playerRangeCorrectionVec.add(0, this.getFinalGravity(), 0);
                    selfVec = selfVec.add(playerRangeCorrectionVec);
                }
            }

            // commit velocity
            this.addVelocity(selfVec);
        }
    }
}
