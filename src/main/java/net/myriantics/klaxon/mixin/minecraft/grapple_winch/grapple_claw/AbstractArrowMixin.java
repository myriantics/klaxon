package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    public AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setPos(DDD)V")
    )
    private void klaxon$dontSetPosForGrappleClawsWithHookedEntities(AbstractArrow instance, double x, double y, double z, Operation<Void> original) {
        if ((Object) this instanceof GrappleClawEntity grappleClaw && grappleClaw.klaxon$getHookedEntity() instanceof Entity hooked) {
            original.call(instance, hooked.getX(), hooked.getY() + grappleClaw.getHookYOffset(), hooked.getZ());
        } else {
            original.call(instance, x, y, z);
        }
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    )
    private void klaxon$cancelGrappleClawVelocityForOnesWithHookedEntities(AbstractArrow instance, Vec3 vec3, Operation<Void> original) {
        if ((Object) this instanceof GrappleClawEntity grappleClaw && grappleClaw.hasHookedEntity()) {
            original.call(instance, Vec3.ZERO);
        } else {
            original.call(instance, vec3);
        }
    }
}
