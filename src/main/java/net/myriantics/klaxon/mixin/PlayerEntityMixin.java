package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.ability.KnockbackModifierComponent;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.component.configuration.MeleeDamageTypeOverrideComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    // for future reference
    // bl in PlayerEntity tells if attack is fully charged
    // bl2 tells if its a knockback hit
    // bl3 tells if its a crit
    // bl4 tells if its a sweeping
    // bl5 is fire aspect
    // bl6 tells if the attack was successful

    // this is how it was in 1.20.1 idk if thats how it works now lol

    @ModifyVariable(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;")
    )
    // ordinal 2 selects boolean #3 (bl3)
    // Switches the player's attacking damage type to Hammer Walloping if they crit with a hammer, otherwise uses Hammer Bonking if they do a regular hit.
    private DamageSource klaxon$applyMeleeDamageTypeComponentOverrides(DamageSource value, @Local(ordinal = 0) boolean fullyCharged, @Local(ordinal = 2) boolean willCrit, @Local(ordinal = 1) boolean knockbackHit) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        ItemStack weaponStack = player.getWeaponStack();

        // check for overridden damage type on weapon stack - if so, apply the override
        MeleeDamageTypeOverrideComponent damageTypeOverride = MeleeDamageTypeOverrideComponent.get(weaponStack);
        if (damageTypeOverride != null) {
            value = KlaxonDamageTypes.getAttackingDamageSource(player, damageTypeOverride.damageType());
        }

        // test for shield penetration component - check if it should run based on critical hit status
        ShieldBreachingComponent shieldPenetration = ShieldBreachingComponent.get(weaponStack);
        if (shieldPenetration != null && shieldPenetration.shouldFire(willCrit, fullyCharged)) {
            value = KlaxonDamageTypes.getAttackingDamageSource(player, shieldPenetration.damageType());
        }

        // check for knockback modifier component
        KnockbackModifierComponent knockbackModifier = KnockbackModifierComponent.get(weaponStack);
        if (knockbackModifier != null && knockbackModifier.shouldFire(knockbackHit) && knockbackModifier.damageType() != null) {
            value = KlaxonDamageTypes.getAttackingDamageSource(player, knockbackModifier.damageType());
        }

        return value;
    }

    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 0)
    )
    private void klaxon$modifyKnockbackStrength(LivingEntity instance, double strength, double x, double z, Operation<Void> original, @Local(ordinal = 1) boolean knockbackHit) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        ItemStack weaponStack = player.getWeaponStack();

        // apply knockback modifier effects
        KnockbackModifierComponent knockbackModifier = KnockbackModifierComponent.get(weaponStack);
        if (knockbackModifier != null && knockbackModifier.shouldFire(knockbackHit)) {
            strength *= knockbackModifier.multiplier();
        }

        original.call(instance, strength, x, z);
    }
}
