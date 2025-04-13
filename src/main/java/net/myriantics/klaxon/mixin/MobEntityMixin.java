package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.component.ability.ShieldPenetrationComponent;
import net.myriantics.klaxon.component.configuration.DamageTypeOverrideComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonDamageTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    // Apply transformations from DamageTypeOverrideComponent & ShieldPenetrationComponent to mob entity attacks
    @ModifyExpressionValue(
            method = "tryAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSources;mobAttack(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/damage/DamageSource;",
                    ordinal = 0))
    private DamageSource klaxon$applyDamageTypeComponentTransformations(DamageSource original, @Local(ordinal = 0, argsOnly = true) Entity target) {

        // yoink
        if (original.getAttacker() instanceof MobEntity attacker) {
            ItemStack weaponStack = attacker.getWeaponStack();

            // replace damage type if present
            DamageTypeOverrideComponent damageTypeOverrideComponent = DamageTypeOverrideComponent.get(weaponStack);
            if (damageTypeOverrideComponent != null) {
                original = KlaxonDamageTypes.getAttackingDamageSource(attacker, damageTypeOverrideComponent.damageType());
            }

            // replace damage type with shield penetrating variant if present
            ShieldPenetrationComponent shieldPenetrationComponent = ShieldPenetrationComponent.get(weaponStack);
            if (shieldPenetrationComponent != null && shieldPenetrationComponent.shouldFire(attacker.getType().isIn(KlaxonEntityTypeTags.HEAVY_HITTERS), true)) {
                original = KlaxonDamageTypes.getAttackingDamageSource(attacker, shieldPenetrationComponent.damageType());
            }
        }

        return original;
    }
}
