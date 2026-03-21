package net.myriantics.klaxon.mixin.minecraft.item_components;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.component.configuration.MeleeDamageTypeOverrideComponent;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    // Apply transformations from DamageTypeOverrideComponent & ShieldPenetrationComponent to mob entity attacks
    @ModifyExpressionValue(
            method = "doHurtTarget",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;mobAttack(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/damagesource/DamageSource;",
                    ordinal = 0))
    private DamageSource klaxon$applyMeleeDamageTypeComponentOverrides(DamageSource original, @Local(ordinal = 0, argsOnly = true) Entity target) {
        ItemStack weaponStack = this.getWeaponItem();

        // check for overridden damage type on weapon stack - if so, apply the override
        MeleeDamageTypeOverrideComponent damageTypeOverride = MeleeDamageTypeOverrideComponent.get(weaponStack);
        if (damageTypeOverride != null) {
            this.damageSources().damageTypes.getHolder(damageTypeOverride.damageType()).ifPresent(
                    entry -> KlaxonDamageTypes.modifyDamageSourceType(original, entry
                    ));
        }

        // replace damage type with shield breaching variant if present
        ShieldBreachingComponent shieldBreachingComponent = ShieldBreachingComponent.get(weaponStack);
        if (shieldBreachingComponent != null && shieldBreachingComponent.shouldFire(this.getType().is(KlaxonEntityTypeTags.HEAVY_HITTERS), true, EnchantmentHelper.has(weaponStack, EnchantmentEffectComponents.KNOCKBACK))) {
            if (shieldBreachingComponent.damageType().isPresent()) {
                Optional<Holder.Reference<DamageType>> entry = this.damageSources().damageTypes.getHolder(shieldBreachingComponent.damageType().get());
                if (entry.isPresent()) {
                    KlaxonDamageTypes.modifyDamageSourceType(original, entry.get());
                }
            }
            ((DamageSourceMixinAccess) original).klaxon$setShieldBreaching(true);
        }

        return original;
    }
}
