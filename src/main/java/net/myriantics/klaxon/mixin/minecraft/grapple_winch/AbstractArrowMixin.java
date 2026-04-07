package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawBlockDestructionHelper;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    public AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;applyGravity()V")
    )
    private void klaxon$cancelGravityIfRetracting(AbstractArrow instance, Operation<Void> original) {

        if (instance instanceof GrapplingHook hook && this.level() instanceof ServerLevel serverWorld) {
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;")
    )
    private BlockHitResult klaxon$replaceRaycastTypeIfNeeded(Level instance, ClipContext raycastContext, Operation<BlockHitResult> original) {
        return (Object) this instanceof GrappleClawEntity grappleClaw
                ? GrappleClawBlockDestructionHelper.raycast(grappleClaw, raycastContext.getFrom(), raycastContext.getTo(), true)
                : original.call(instance, raycastContext);
    }

    @WrapOperation(
            method = "playerTouch",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;tryPickup(Lnet/minecraft/world/entity/player/Player;)Z")
    )
    private boolean klaxon$tryFastReload(AbstractArrow instance, Player player, Operation<Boolean> original) {
        if (instance instanceof GrapplingHook hook && hook.klaxon$getHookedEntity() == null) {
            if (hook.klaxon$tryFastReload(player, player.getMainHandItem()) || hook.klaxon$tryFastReload(player, player.getOffhandItem())) {
                return false;
            }
        }
        return original.call(instance, player);
    }
}
