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

    // stored separately in order to allow grapple winch to work even if grapple claw is unloaded on client
    @Unique
    private Vec3d klaxon$fallbackGrappleClawPos = null;

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
    public Vec3d klaxon$getFallbackGrappleClawPos() {
        return klaxon$fallbackGrappleClawPos;
    }

    @Override
    public void klaxon$setFallbackGrappleClawPos(Vec3d grappleClawPos) {
        this.klaxon$fallbackGrappleClawPos = grappleClawPos;
    }

    @Inject(
            method = "tickMovement",
            at = @At(value = "HEAD")
    )
    private void klaxon$tickGrappleWinchMovement(CallbackInfo ci) {

        // only run player movement logic when we have an active anchored grapple claw
        if (getWorld().isClient()) {
            Vec3d selfVec = Vec3d.ZERO;

            // initialize values
            Vec3d playerToClawVec;
            double clawDistance;
            double maxRangeSquared = GrappleClawEntity.MAX_RANGE_SQUARED;
            double targetRangeSquared;
            boolean shouldMove;

            // update values based on whether the claw is loaded clientside or not
            if (klaxon$grappleClaw != null) {
                playerToClawVec = klaxon$grappleClaw.getPos().subtract(this.getPos());
                clawDistance = getPos().squaredDistanceTo(klaxon$grappleClaw.getPos());
                targetRangeSquared = klaxon$grappleClaw.getTargetRangeSquared();
                shouldMove = klaxon$grappleClaw.isAnchored();
            } else if (klaxon$fallbackGrappleClawPos != null) {
                playerToClawVec = klaxon$fallbackGrappleClawPos.subtract(this.getPos());
                clawDistance = getPos().squaredDistanceTo(klaxon$fallbackGrappleClawPos);
                targetRangeSquared = maxRangeSquared;
                shouldMove = true;
            } else {
                // return if both checks fail
                return;
            }

            // make sure grapple claw is loaded and anchored
            if (shouldMove) {

                // get movement vectors and normalize them
                playerToClawVec = playerToClawVec.normalize();
                Vec3d playerFacingVec = this.getRotationVec(1.0f).normalize();

                // tick retraction movement
                if (klaxon$isRetractingGrappleWinch) {

                    // transform movement vectors
                    Vec3d playerToClawRetractionVec = playerToClawVec.multiply(2./20);
                    // player can direct movement with facing direction to combat getting stuck under ledges
                    Vec3d playerFacingRetractionVec = playerFacingVec.multiply(1./20).multiply(this.isSprinting() ? 1.5 : 1);

                    // add vectors to self vector
                    // owner goes towards claw if not sneaking, away if they are sneaking
                    if (!this.isSneaking()) {
                        selfVec = selfVec.add(playerToClawRetractionVec).add(playerFacingRetractionVec);
                    }
                    // no custom logic is needed if the player is sneaking because they will just fall until they hit the max or target range
                }

                // apply velocity to player if they go past target range
                // retraction is only capped at the max range
                // also this is a dope ass spot to use ternary operators omg
                if (clawDistance > (klaxon$isRetractingGrappleWinch ? maxRangeSquared : targetRangeSquared)) {
                    Vec3d playerRangeCorrectionVec = playerToClawVec.multiply(0.1);
                    // when i say a limit i mean it haha
                    if (clawDistance > maxRangeSquared) playerRangeCorrectionVec = playerRangeCorrectionVec.multiply(Math.pow(clawDistance / maxRangeSquared, 3));
                    playerRangeCorrectionVec = playerRangeCorrectionVec.add(0, this.getFinalGravity(), 0);
                    selfVec = selfVec.add(playerRangeCorrectionVec);
                }
            }

            // commit velocity
            this.addVelocity(selfVec);
        }
    }
}
