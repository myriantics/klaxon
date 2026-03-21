package net.myriantics.klaxon.mixin.minecraft.item_components.knockback_hit_modifier;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.component.ability.KnockbackHitModifierComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {
    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V", ordinal = 0)
    )
    private void klaxon$modifyKnockbackStrength(LivingEntity instance, double strength, double x, double z, Operation<Void> original, @Local(ordinal = 1) boolean knockbackHit) {
        Player player = ((Player) (Object) this);
        ItemStack weaponStack = player.getWeaponItem();

        // apply knockback modifier effects
        KnockbackHitModifierComponent knockbackModifier = KnockbackHitModifierComponent.get(weaponStack);
        if (knockbackModifier != null && knockbackModifier.shouldFire(knockbackHit)) {
            strength *= knockbackModifier.multiplier();
        }

        original.call(instance, strength, x, z);
    }
}
