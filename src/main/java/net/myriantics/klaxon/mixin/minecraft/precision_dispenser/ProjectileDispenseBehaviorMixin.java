package net.myriantics.klaxon.mixin.minecraft.precision_dispenser;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ProjectileItem;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProjectileDispenseBehavior.class)
public abstract class ProjectileDispenseBehaviorMixin {
    @WrapOperation(
            method = "execute",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileItem;shoot(Lnet/minecraft/world/entity/projectile/Projectile;DDDFF)V")
    )
    private void klaxon$dispenseWithNoRandomnessIfPossible(ProjectileItem instance, Projectile projectile, double x, double y, double z, float velocity, float inaccuracy, Operation<Void> original, @Local(argsOnly = true) BlockSource source) {
        if (source.blockEntity() instanceof PrecisionDispenserBlockEntity precisionDispenser) {
            original.call(instance, projectile, x, y, z, velocity, precisionDispenser.getInaccuracy(inaccuracy));
        } else {
            original.call(instance, projectile, x, y, z, velocity, inaccuracy);
        }
    }
}
