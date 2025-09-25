package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchConnectionData;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityGrappleAccess {

    @Unique
    private GrappleClawEntity klaxon$grappleClaw = null;

    @Unique
    private boolean klaxon$isRetractingGrappleWinch = false;

    // stored separately in order to allow grapple winch to work even if grapple claw is unloaded on client
    @Nullable
    @Unique
    private GrappleWinchConnectionData klaxon$grappleWinchFallbackData = null;

    @Unique
    private double klaxon$currentWinchCableLength = getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public @Nullable GrappleClawEntity klaxon$getGrappleClaw() {
        if (this.klaxon$grappleClaw != null && this.klaxon$grappleClaw.isRemoved()) {
            this.klaxon$grappleClaw = null;
        }

        return this.klaxon$grappleClaw;
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
        this.klaxon$currentWinchCableLength = Math.clamp(currentWinchCableLength, 0, getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH));
    }

    @Override
    public void klaxon$resetWinchCableLength() {
        GrappleClawEntity grappleClaw = klaxon$getGrappleClaw();
        GrappleWinchConnectionData fallbackData = klaxon$getWinchFallbackData();

        if (grappleClaw != null) {
            this.klaxon$setCurrentWinchCableLength(grappleClaw.getPos().distanceTo(this.getPos()));
        } else if (fallbackData != null) {
            this.klaxon$setCurrentWinchCableLength(fallbackData.clawPos().distanceTo(this.getPos()));
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
            double maxRangeBlocks = getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);
            double currentWinchCableLength = klaxon$getCurrentWinchCableLength();
            boolean shouldMove;

            // update values based on whether the claw is loaded clientside or not
            if (grappleClaw != null) {
                playerToClawVec = grappleClaw.getPos().subtract(this.getPos());
                clawDistance = getPos().distanceTo(grappleClaw.getPos());
                shouldMove = grappleClaw.isAnchored();
            } else if (fallbackData != null) {
                playerToClawVec = fallbackData.clawPos().subtract(this.getPos());
                clawDistance = getPos().distanceTo(fallbackData.clawPos());
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
                if (clawDistance > (klaxon$isRetractingGrappleWinch ? maxRangeBlocks : currentWinchCableLength)) {
                    Vec3d playerRangeCorrectionVec = playerToClawVec.multiply(0.1);
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

        // write grapple claw data to player nbt like you would a vehicle
        if ((Object) this instanceof ServerPlayerEntity serverPlayer) {
            GrappleClawEntity grappleClaw = this.klaxon$getGrappleClaw();

            if (grappleClaw != null) {
                NbtCompound grappleClawCompound = new NbtCompound();
                grappleClaw.saveNbt(grappleClawCompound);
                nbt.put(KlaxonNBTIds.ATTACHED_GRAPPLE_CLAW, grappleClawCompound);
            } else {
                nbt.remove(KlaxonNBTIds.ATTACHED_GRAPPLE_CLAW);
            }
        }

        nbt.putDouble(KlaxonNBTIds.CURRENT_WINCH_CABLE_LENGTH, this.klaxon$currentWinchCableLength);
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At(value = "TAIL")
    )
    private void klaxon$readPersistentData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(KlaxonNBTIds.ATTACHED_GRAPPLE_CLAW)) {
            NbtCompound grappleClawCompound = nbt.getCompound(KlaxonNBTIds.ATTACHED_GRAPPLE_CLAW);
            Optional<Entity> maybeGrappleClaw = EntityType.getEntityFromNbt(grappleClawCompound, getWorld());
            if (maybeGrappleClaw.isPresent() && maybeGrappleClaw.get() instanceof GrappleClawEntity grappleClaw) {
                this.klaxon$setGrappleClaw(grappleClaw);
                if ((PlayerEntity) (Object) this instanceof ServerPlayerEntity serverPlayer) {
                    grappleClaw.attachCable(serverPlayer);
                }
            }
        }

        if (nbt.containsUuid(KlaxonNBTIds.CURRENT_WINCH_CABLE_LENGTH)) {
            this.klaxon$currentWinchCableLength = nbt.getDouble(KlaxonNBTIds.CURRENT_WINCH_CABLE_LENGTH);
        }
    }
}
