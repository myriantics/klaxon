package net.myriantics.klaxon.mixin.item_components.knockback_hit_modifier;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.component.ability.KnockbackHitModifierComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 0)
    )
    private void klaxon$modifyKnockbackStrength(LivingEntity instance, double strength, double x, double z, Operation<Void> original, @Local(ordinal = 1) boolean knockbackHit) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        ItemStack weaponStack = player.getWeaponStack();

        // apply knockback modifier effects
        KnockbackHitModifierComponent knockbackModifier = KnockbackHitModifierComponent.get(weaponStack);
        if (knockbackModifier != null && knockbackModifier.shouldFire(knockbackHit)) {
            strength *= knockbackModifier.multiplier();
        }

        original.call(instance, strength, x, z);
    }
}
