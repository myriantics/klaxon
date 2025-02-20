package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.util.KlaxonDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract ItemStack getMainHandStack();

    @Shadow public abstract void damageShield(float amount);

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    // Allows hammer walloping damage to disable shields and deal damage through them - only activated on crit
    @ModifyExpressionValue(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z"))
    public boolean hammerWallopingOverride(boolean original, @Local(argsOnly = true) DamageSource damageSource, @Local(argsOnly = true) float amount) {
        // if its already blocked by shield dont mess with it - walloping passes through this because it's tagged as bypasses_shield
        if (original) return true;

        if (damageSource.isOf(KlaxonDamageTypes.HAMMER_WALLOPING) && damageSource.getSource() instanceof PlayerEntity attacker) {
            damageShield(amount);
            takeShieldHit(attacker);

            // we have to call this independently because the hammer itself doesn't disable shields
            // it has to be walloping damage to pierce through a shield
            if (((Object)this) instanceof PlayerEntity player) {
                player.disableShield();
            }

            // we have our own custom processing, we don't need to run the regular shield disabling stuff
            return false;
        }
        // if it's not walloping
        return false;
    }
}