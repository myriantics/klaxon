package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchConnectionData;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw.EnderDragonEntityAccessor;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityGrappleAccess {

    @Unique
    private double klaxon$currentWinchCableLength = getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

    @Unique
    private @Nullable GrappleWinchConnectionData klaxon$connectionData = null;

    @Unique
    private @Nullable GrappleClawEntity klaxon$attachedGrappleClaw = null;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean klaxon$isRetracting() {
        return klaxon$hasActiveConnection() && this.isUsingItem() && this.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH);
    }

    @Override
    public double klaxon$getCurrentWinchCableLength() {
        return klaxon$currentWinchCableLength;
    }

    @Override
    public void klaxon$setConnectionData(@Nullable GrappleWinchConnectionData connectionData) {
        this.klaxon$connectionData = connectionData;
    }

    @Override
    public @Nullable GrappleWinchConnectionData klaxon$getConnectionData() {
        return klaxon$connectionData;
    }

    @Override
    public @Nullable GrappleClawEntity klaxon$getGrappleClaw() {
        return klaxon$attachedGrappleClaw;
    }

    @Override
    public void klaxon$setGrappleClaw(@Nullable GrappleClawEntity grappleClaw) {
        this.klaxon$attachedGrappleClaw = grappleClaw;
    }

    @Override
    public void klaxon$setCurrentWinchCableLength(double currentWinchCableLength) {
        this.klaxon$currentWinchCableLength = Math.clamp(currentWinchCableLength, 0, getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH));
    }

    @Override
    public boolean klaxon$hasActiveConnection() {
        return getWorld().isClient() ? klaxon$getConnectionData() != null : klaxon$attachedGrappleClaw != null && !klaxon$attachedGrappleClaw.isRemoved();
    }

    @Override
    public void klaxon$resetWinchCableLength() {
        GrappleClawEntity grappleClaw = klaxon$getGrappleClaw();
        GrappleWinchConnectionData fallbackData = klaxon$getConnectionData();

        if (grappleClaw != null) {
            this.klaxon$setCurrentWinchCableLength(grappleClaw.getPos().distanceTo(this.getPos()));
        } else if (fallbackData != null) {
            this.klaxon$setCurrentWinchCableLength(fallbackData.grappleClawPos().distanceTo(this.getPos()));
        }
    }

    @Inject(
            method = "tickMovement",
            at = @At(value = "HEAD")
    )
    private void klaxon$tickGrappleWinchMovement(CallbackInfo ci) {

        // only run player movement logic when we have an active anchored grapple claw
        if (this.getWorld().isClient() && klaxon$hasActiveConnection()) {
            Vec3d selfVec = Vec3d.ZERO;

            // get grapple claw through the getter in order to sync it with UUID
            // also get fallback connectionData in case claw is unloaded on client side
            @Nullable GrappleClawEntity grappleClaw = klaxon$getGrappleClaw();
            @Nullable GrappleWinchConnectionData fallbackData = klaxon$getConnectionData();

            boolean isRetracting = klaxon$isRetracting();
            double maxWinchCableLength = getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);
            double currentWinchCableLength = klaxon$getCurrentWinchCableLength();

            // initialize values
            Vec3d playerToClawVec;
            double clawDistance;
            boolean shouldMove;

            // update values based on whether the claw is loaded clientside or not
            if (grappleClaw != null && !grappleClaw.isRemoved()) {
                playerToClawVec = grappleClaw.getPos().subtract(this.getEyePos());
                clawDistance = getEyePos().distanceTo(grappleClaw.getPos());
                shouldMove = (fallbackData != null && fallbackData.isClawAnchored()) || grappleClaw.isAnchored();
            } else if (fallbackData != null) {
                playerToClawVec = fallbackData.grappleClawPos().subtract(this.getEyePos());
                clawDistance = getEyePos().distanceTo(fallbackData.grappleClawPos());
                shouldMove = fallbackData.isClawAnchored();
            } else {
                // return if both checks fail
                return;
            }

            // update winch cable length
            if (isRetracting || (isOnGround() && clawDistance > klaxon$getCurrentWinchCableLength()) ) {
                this.klaxon$setCurrentWinchCableLength(clawDistance);
            }

            // make sure grapple claw is loaded and anchored
            if (shouldMove && !EntityWeightHelper.isHeavy(this)) {

                // get movement vectors and normalize them
                playerToClawVec = playerToClawVec.normalize();
                Vec3d playerFacingVec = this.getRotationVec(1.0f).normalize();

                // tick retraction movement
                if (klaxon$isRetracting()) {

                    // transform movement vectors
                    Vec3d playerToClawRetractionVec = playerToClawVec.multiply(2./20);
                    // player can direct movement with facing direction to combat getting stuck under ledges
                    Vec3d playerFacingRetractionVec = playerFacingVec.multiply(1./20).multiply(this.isSprinting() ? 1.5 : 1);

                    // add vectors to self vector
                    if (!this.isSneaking()) {
                        selfVec = selfVec.add(playerToClawRetractionVec).add(playerFacingRetractionVec);
                    }
                }

                // apply velocity to player if they go past target range
                // retraction is only capped at the max range
                // cable length is also less regulated when sneaking & retracting so that players can descend with the grapple winch
                if (clawDistance > ((isSneaking() && isRetracting) || isOnGround()
                        ? maxWinchCableLength
                        : Math.min(maxWinchCableLength, currentWinchCableLength)
                )) {
                    Vec3d playerRangeCorrectionVec = playerToClawVec.multiply(0.1);
                    playerRangeCorrectionVec = playerRangeCorrectionVec.add(0, this.getFinalGravity(), 0);
                    selfVec = selfVec.add(playerRangeCorrectionVec);
                }
            }

            // commit velocity
            this.addVelocity(selfVec);
        }
    }

    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    )
    private boolean klaxon$tryFastReloadWhenHittingEntity(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
        @Nullable GrappleClawEntity grappleClaw = this.klaxon$getGrappleClaw();
        @Nullable ItemStack weaponStack = source.getWeaponStack();

        Entity attackedEntity = instance instanceof EnderDragonPart part
                ? ((EnderDragonEntityAccessor) part.owner).getBody()
                : instance;

        // try to fast reload the grapple claw attached to the entity if it's attached
        if (source.isDirect() && grappleClaw != null && weaponStack != null && grappleClaw.hookedEntityHandler.hookedEntityMatches(attackedEntity)) {
            if (grappleClaw.tryFastReload((PlayerEntity) (Object) this, weaponStack)) {
                return false;
            }
        }

        return original.call(instance, source, amount);
    }
}
