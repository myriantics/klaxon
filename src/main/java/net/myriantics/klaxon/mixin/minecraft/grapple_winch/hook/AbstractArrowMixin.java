package net.myriantics.klaxon.mixin.minecraft.grapple_winch.hook;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    public AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;lerpRotation(FF)F")
    )
    private float klaxon$cancelRotationUpdatesWhenRetracting(float val1, float val2, Operation<Float> original) {
        if (this instanceof GrapplingHook hook) {
            GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(this.level());
            @Nullable GrappleWinchConnection connection = manager.fromHook(hook);
            if (connection != null && connection.isRetracting()) {
                return -val2;
            }
        }

        return original.call(val1, val2);
    }
}
