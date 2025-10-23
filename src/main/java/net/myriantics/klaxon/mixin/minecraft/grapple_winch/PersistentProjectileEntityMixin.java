package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {

    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;applyGravity()V")
    )
    private void klaxon$cancelGravityIfRetracting(PersistentProjectileEntity instance, Operation<Void> original) {
        if (instance instanceof GrappleClawEntity grappleClaw) {
            // no need to apply gravity if it's anchored - this means it's either in a block or grappling an entity
            if (grappleClaw.isAnchored()) {
                return;
            }

            // don't apply gravity if it's being retracted
            @Nullable PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) grappleClaw.getAttachedPlayer();
            if (access != null && grappleClaw.equals(access.klaxon$getGrappleClaw()) && access.klaxon$isRetracting()) {
                return;
            }
        }

        // otherwise, gravity works as normal
        original.call(instance);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;raycast(Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;")
    )
    private BlockHitResult klaxon$replaceRaycastTypeIfNeeded(World instance, RaycastContext raycastContext, Operation<BlockHitResult> original) {
        return (Object) this instanceof GrappleClawEntity grappleClaw
                ? grappleClaw.blockDestructionHandler.raycast(raycastContext.getStart(), raycastContext.getEnd())
                : original.call(instance, raycastContext);
    }
}
