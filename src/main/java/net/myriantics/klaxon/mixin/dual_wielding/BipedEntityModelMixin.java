package net.myriantics.klaxon.mixin.dual_wielding;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.myriantics.klaxon.util.LivingEntityMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> {

    @Shadow protected abstract void animateArms(T entity, float animationProgress);

    @Inject(
            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateArms(Lnet/minecraft/entity/LivingEntity;F)V")
    )
    public void klaxon$animateNonPreferredArm(T livingEntity, float f, float g, float animationProgress, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof LivingEntityMixinAccess access && access.klaxon$isDualWielding()) animateArms(livingEntity, animationProgress * -1.0f);
    }

    @ModifyExpressionValue(
            method = "animateArms",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;getPreferredArm(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/Arm;")
    )
    public Arm klaxon$swapArmIfNegative(Arm original, @Local(argsOnly = true) LocalFloatRef animationProgress) {
        if (animationProgress.get() < 0) {
            animationProgress.set(animationProgress.get() * -1.0f);

            return original.getOpposite();
        }

        return original;
    }
}
