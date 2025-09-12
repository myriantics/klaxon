package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.util.grapple_winch.GrappleWinchConnectionData;
import net.myriantics.klaxon.util.grapple_winch.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityGrappleAccess {

    @Shadow @Final private PlayerInventory inventory;
    @Unique
    private GrappleClawEntity klaxon$grappleClaw = null;

    @Unique
    private UUID klaxon$winchConnectionUUID = null;

    @Unique
    private boolean klaxon$isRetractingGrappleWinch = false;

    // stored separately in order to allow grapple winch to work even if grapple claw is unloaded on client
    @Nullable
    @Unique
    private GrappleWinchConnectionData klaxon$grappleWinchFallbackData = null;

    @Unique
    private double klaxon$currentWinchCableLength = GrappleClawEntity.MAX_RANGE_SQUARED;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public GrappleClawEntity klaxon$getGrappleClaw() {
        if (this.klaxon$grappleClaw != null && this.klaxon$grappleClaw.isRemoved()) {
            this.klaxon$grappleClaw = null;
        } else if (this.klaxon$winchConnectionUUID != null && this.getWorld() instanceof ServerWorld serverWorld) {
            Entity winchConnection = serverWorld.getEntity(this.klaxon$winchConnectionUUID);

            if (winchConnection instanceof GrappleClawEntity grappleClaw) {
                // update grapple claw if we succeed
                this.klaxon$grappleClaw = grappleClaw;
            }
        }

        return this.klaxon$grappleClaw;
    }

    @Override
    public void klaxon$setGrappleClaw(GrappleClawEntity grappleClaw) {
        this.klaxon$grappleClaw = grappleClaw;
        if (grappleClaw != null) {
            this.klaxon$winchConnectionUUID = grappleClaw.getUuid();
        }
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
    public GrappleWinchConnectionData klaxon$getWinchFallbackData() {
        return klaxon$grappleWinchFallbackData;
    }

    @Override
    public void klaxon$setWinchConnectionData(GrappleWinchConnectionData winchFallbackData) {
        this.klaxon$grappleWinchFallbackData = winchFallbackData;
    }

    @Override
    public boolean klaxon$hasActiveConnection() {
        return klaxon$getGrappleClaw() != null || klaxon$getWinchFallbackData() != null;
    }

    @Override
    public double klaxon$getCurrentWinchCableLength() {
        return klaxon$currentWinchCableLength;
    }

    @Override
    public void klaxon$setCurrentWinchCableLength(double currentWinchCableLength) {
        this.klaxon$currentWinchCableLength = Math.clamp(currentWinchCableLength, 0, GrappleClawEntity.MAX_RANGE_SQUARED);
    }

    @Override
    public void klaxon$resetWinchCableLength() {
        GrappleClawEntity grappleClaw = klaxon$getGrappleClaw();
        GrappleWinchConnectionData fallbackData = klaxon$getWinchFallbackData();

        if (grappleClaw != null) {
            this.klaxon$setCurrentWinchCableLength(grappleClaw.getPos().squaredDistanceTo(this.getPos()));
        } else if (fallbackData != null) {
            this.klaxon$setCurrentWinchCableLength(fallbackData.clawPos().squaredDistanceTo(this.getPos()));
        }
    }

    @Inject(
            method = "tickMovement",
            at = @At(value = "HEAD")
    )
    private void klaxon$tickGrappleWinchMovement(CallbackInfo ci) {

        // only run player movement logic when we have an active anchored grapple claw
        if (getWorld().isClient()) {
            Vec3d selfVec = Vec3d.ZERO;

            // get grapple claw through the getter in order to sync it with UUID
            // also get fallback connectionData in case claw is unloaded on client side
            @Nullable GrappleClawEntity grappleClaw = klaxon$getGrappleClaw();
            @Nullable GrappleWinchConnectionData fallbackData = klaxon$getWinchFallbackData();

            // initialize values
            Vec3d playerToClawVec;
            double clawDistance;
            double maxRangeSquared = GrappleClawEntity.MAX_RANGE_SQUARED;
            double currentWinchCableLength = klaxon$getCurrentWinchCableLength();
            boolean shouldMove;

            // update values based on whether the claw is loaded clientside or not
            if (grappleClaw != null) {
                playerToClawVec = grappleClaw.getPos().subtract(this.getPos());
                clawDistance = getPos().squaredDistanceTo(grappleClaw.getPos());
                shouldMove = grappleClaw.isAnchored();
            } else if (fallbackData != null) {
                playerToClawVec = fallbackData.clawPos().subtract(this.getPos());
                clawDistance = getPos().squaredDistanceTo(fallbackData.clawPos());
                shouldMove = fallbackData.isWinchAnchored();
            } else {
                // return if both checks fail
                return;
            }

            // make sure grapple claw is loaded and anchored
            if (shouldMove && !EntityWeightHelper.isHeavy(this)) {

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
                    } else {
                        Vec3d correctedFacingVec = playerFacingRetractionVec.multiply(1, playerFacingRetractionVec.getY() > 0 ? 0 : 1, 1).negate().multiply(0.65);
                        selfVec = selfVec.add(correctedFacingVec);
                    }
                }

                // apply velocity to player if they go past target range
                // retraction is only capped at the max range
                // also this is a dope ass spot to use ternary operators omg
                if (clawDistance > (klaxon$isRetractingGrappleWinch ? maxRangeSquared : currentWinchCableLength)) {
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

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At(value = "TAIL")
    )
    private void klaxon$writePersistentData(NbtCompound nbt, CallbackInfo ci) {
        if (this.klaxon$winchConnectionUUID != null) {
            nbt.putUuid("klaxon.winch_connection", this.klaxon$winchConnectionUUID);
        }

        nbt.putDouble("klaxon.current_winch_cable_length", this.klaxon$currentWinchCableLength);
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At(value = "TAIL")
    )
    private void klaxon$readPersistentData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid("klaxon.winch_connection")) {
            this.klaxon$winchConnectionUUID = nbt.getUuid("klaxon.winch_connection");
            this.klaxon$grappleClaw = null;
        }

        if (nbt.containsUuid("klaxon.current_winch_cable_length")) {
            this.klaxon$currentWinchCableLength = nbt.getDouble("klaxon.current_winch_cable_length");
        }
    }
}
