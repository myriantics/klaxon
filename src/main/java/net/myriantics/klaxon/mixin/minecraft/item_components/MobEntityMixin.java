package net.myriantics.klaxon.mixin.minecraft.item_components;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.component.configuration.MeleeDamageTypeOverrideComponent;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // Apply transformations from DamageTypeOverrideComponent & ShieldPenetrationComponent to mob entity attacks
    @ModifyExpressionValue(
            method = "tryAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSources;mobAttack(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/damage/DamageSource;",
                    ordinal = 0))
    private DamageSource klaxon$applyMeleeDamageTypeComponentOverrides(DamageSource original, @Local(ordinal = 0, argsOnly = true) Entity target) {
        ItemStack weaponStack = this.getWeaponStack();

        // check for overridden damage type on weapon stack - if so, apply the override
        MeleeDamageTypeOverrideComponent damageTypeOverride = MeleeDamageTypeOverrideComponent.get(weaponStack);
        if (damageTypeOverride != null) {
            this.getDamageSources().registry.getEntry(damageTypeOverride.damageType()).ifPresent(
                    entry -> KlaxonDamageTypes.modifyDamageSourceType(original, entry
                    ));
        }

        // replace damage type with shield breaching variant if present
        ShieldBreachingComponent shieldBreachingComponent = ShieldBreachingComponent.get(weaponStack);
        if (shieldBreachingComponent != null && shieldBreachingComponent.shouldFire(this.getType().isIn(KlaxonEntityTypeTags.HEAVY_HITTERS), true, EnchantmentHelper.hasAnyEnchantmentsWith(weaponStack, EnchantmentEffectComponentTypes.KNOCKBACK))) {
            if (shieldBreachingComponent.damageType().isPresent()) {
                Optional<RegistryEntry.Reference<DamageType>> entry = this.getDamageSources().registry.getEntry(shieldBreachingComponent.damageType().get());
                if (entry.isPresent()) {
                    KlaxonDamageTypes.modifyDamageSourceType(original, entry.get());
                }
            }
            ((DamageSourceMixinAccess) original).klaxon$setShieldBreaching(true);
        }

        return original;
    }
}
