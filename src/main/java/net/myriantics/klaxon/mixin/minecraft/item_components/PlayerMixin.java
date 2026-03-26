package net.myriantics.klaxon.mixin.minecraft.item_components;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.component.ability.KnockbackHitModifierComponent;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.component.configuration.MeleeDamageTypeOverrideComponent;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;")
    )
    // ordinal 2 selects boolean #3 (bl3)
    // Changes melee damage type of attacking player based on item components.
    private DamageSource klaxon$applyMeleeDamageTypeComponentOverrides(DamageSource original, @Local(ordinal = 0) boolean fullyCharged, @Local(ordinal = 2) boolean willCrit, @Local(ordinal = 1) boolean knockbackHit) {
        Player player = ((Player) (Object) this);
        ItemStack weaponStack = player.getWeaponItem();

        // check for overridden damage type on weapon stack - if so, apply the override
        MeleeDamageTypeOverrideComponent damageTypeOverride = MeleeDamageTypeOverrideComponent.get(weaponStack);
        if (damageTypeOverride != null) {
            this.damageSources().damageTypes.getHolder(damageTypeOverride.damageType()).ifPresent(
                    entry -> KlaxonDamageTypes.modifyDamageSourceType(original, entry
            ));
        }

        // replace damage type with shield breaching variant if present
        ShieldBreachingComponent shieldBreachingComponent = ShieldBreachingComponent.get(weaponStack);
        if (shieldBreachingComponent != null && shieldBreachingComponent.shouldFire(willCrit, fullyCharged, knockbackHit)) {
            if (shieldBreachingComponent.damageType().isPresent()) {
                Optional<Holder.Reference<DamageType>> entry = this.damageSources().damageTypes.getHolder(shieldBreachingComponent.damageType().get());
                entry.ifPresent(damageTypeReference -> KlaxonDamageTypes.modifyDamageSourceType(original, damageTypeReference));
            }
            ((DamageSourceMixinAccess) original).klaxon$setShieldBreaching(true);
        }

        // check for knockback modifier component - change damage type if present
        KnockbackHitModifierComponent knockbackModifier = KnockbackHitModifierComponent.get(weaponStack);
        if (knockbackModifier != null && knockbackModifier.shouldFire(knockbackHit)) {
            if (knockbackModifier.damageType().isPresent()) {
                this.damageSources().damageTypes.getHolder(knockbackModifier.damageType().get()).ifPresent(
                        entry -> KlaxonDamageTypes.modifyDamageSourceType(original, entry
                        ));
            }
        }

        return original;
    }
}
