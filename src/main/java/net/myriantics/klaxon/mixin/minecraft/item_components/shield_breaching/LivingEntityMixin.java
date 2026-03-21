package net.myriantics.klaxon.mixin.minecraft.item_components.shield_breaching;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract void hurtCurrentlyUsedShield(float amount);

    @Shadow protected abstract void blockUsingShield(LivingEntity attacker);

    // Allows shield penetrating items to disable shields and deal damage through them
    @ModifyExpressionValue(
            method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z")
    )
    public boolean klaxon$shieldBreachingOverride(boolean original, @Local(argsOnly = true) DamageSource damageSource, @Local(argsOnly = true) float amount) {

        // make sure shield would've blocked attack
        if (original
                // make sure attack is actually shield breaching
                && ((DamageSourceMixinAccess) damageSource).klaxon$isShieldBreaching()
                && damageSource.getEntity() instanceof LivingEntity attacker
        ) {
            hurtCurrentlyUsedShield(amount);
            blockUsingShield(attacker);
            if (((Object) this) instanceof Player player) player.disableShield();

            // we have our own custom processing, we don't need to run the regular shield disabling stuff
            return false;
        }

        // no need to retain the original since any positives are filtered out at the start
        return original;
    }

}
