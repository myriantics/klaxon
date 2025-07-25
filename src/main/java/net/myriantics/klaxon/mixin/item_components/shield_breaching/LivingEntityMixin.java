package net.myriantics.klaxon.mixin.item_components.shield_breaching;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract void damageShield(float amount);

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    // Allows shield penetrating items to disable shields and deal damage through them
    @ModifyExpressionValue(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z")
    )
    public boolean klaxon$shieldBreachingOverride(boolean original, @Local(argsOnly = true) DamageSource damageSource, @Local(argsOnly = true) float amount) {

        // make sure shield would've blocked attack
        if (original
                // make sure attack is actually shield breaching
                && ((DamageSourceMixinAccess) damageSource).klaxon$getShieldBreachingComponent() != null
                && damageSource.getAttacker() instanceof LivingEntity attacker
        ) {
            damageShield(amount);
            takeShieldHit(attacker);
            if (((Object) this) instanceof PlayerEntity player) player.disableShield();

            // we have our own custom processing, we don't need to run the regular shield disabling stuff
            return false;
        }

        // no need to retain the original since any positives are filtered out at the start
        return original;
    }

}
