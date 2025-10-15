package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.ItemUsageLockoutTrigger;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
}
