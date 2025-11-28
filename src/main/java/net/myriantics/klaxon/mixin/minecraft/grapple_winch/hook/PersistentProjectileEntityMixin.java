package net.myriantics.klaxon.mixin.minecraft.grapple_winch.hook;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;updateRotation(FF)F")
    )
    private float klaxon$cancelRotationUpdatesWhenRetracting(float val1, float val2, Operation<Float> original) {
        if (this instanceof GrapplingHook hook) {
            GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) this.getWorld()).klaxon$get();
            @Nullable GrappleWinchConnection connection = manager.fromHook(hook);
            if (connection != null && connection.isRetracting()) {
                return -val2;
            }
        }

        return original.call(val1, val2);
    }
}
