package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.ItemUsageLockoutTrigger;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {

    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;inGround:Z",
                    ordinal = 1
            )
    )
    private boolean klaxon$ignoreInGroundIfRetractingGrappleClaw(boolean original) {
        // original being true indicates being stuck in a block, so only call this if it's true
        if (
                original
                && (Object) this instanceof GrappleClawEntity
                && this.getOwner() instanceof ServerPlayerEntity serverPlayer
                && this.equals(((PlayerEntityGrappleAccess) serverPlayer).klaxon$getGrappleClaw())
                && ((PlayerEntityGrappleAccess) serverPlayer).klaxon$isRetracting()
        ) {
            // this returning false indicates that the owner exists and is retracting the grapple winch - so we should tick movement like normal
            return original && !EntityWeightHelper.isHeavy(serverPlayer);
        }

        return original;
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;applyGravity()V")
    )
    private void klaxon$cancelGravityIfRetracting(PersistentProjectileEntity instance, Operation<Void> original) {
        if (
                instance instanceof GrappleClawEntity grappleClaw
                && grappleClaw.getOwner() instanceof PlayerEntityGrappleAccess access
                && this.equals(access.klaxon$getGrappleClaw())
                && access.klaxon$isRetracting()
        ) {
            return;
        } else {
            original.call(instance);
        }
    }

    @ModifyExpressionValue(
            method = "onPlayerCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;tryPickup(Lnet/minecraft/entity/player/PlayerEntity;)Z")
    )
    private boolean klaxon$lockoutItemUse(boolean original, @Local(argsOnly = true) PlayerEntity player) {
        if (original && player instanceof ServerPlayerEntity serverPlayer) {
            // this is needed so players can choose whether they want to recast grapple claw or not
            if (player.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH)) {
                // update usage lockout if true
                KlaxonServerPlayNetworkHandler.send(serverPlayer, new ItemUsageLockoutTrigger());
            }
        }

        return original;
    }
}
