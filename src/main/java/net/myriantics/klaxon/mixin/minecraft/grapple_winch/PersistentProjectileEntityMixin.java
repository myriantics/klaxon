package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawBlockDestructionHelper;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
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

        if (instance instanceof GrapplingHook hook && this.getWorld() instanceof ServerWorld serverWorld) {
            ServerGrappleWinchConnection connection = ServerGrappleWinchConnectionManager.get(serverWorld).fromHook(hook);

            if (connection != null && (connection.isHookAnchored() || connection.isRetracting())) {
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
                ? GrappleClawBlockDestructionHelper.raycast(grappleClaw, raycastContext.getStart(), raycastContext.getEnd(), true)
                : original.call(instance, raycastContext);
    }

    @WrapOperation(
            method = "onPlayerCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;tryPickup(Lnet/minecraft/entity/player/PlayerEntity;)Z")
    )
    private boolean klaxon$tryFastReload(PersistentProjectileEntity instance, PlayerEntity player, Operation<Boolean> original) {
        if (instance instanceof GrapplingHook hook) {
            if (hook.klaxon$tryFastReload(player, player.getMainHandStack()) || hook.klaxon$tryFastReload(player, player.getOffHandStack())) {
                return false;
            }
        }
        return original.call(instance, player);
    }
}
