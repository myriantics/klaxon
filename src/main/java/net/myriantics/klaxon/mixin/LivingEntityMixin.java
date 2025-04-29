package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonEntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract void damageShield(float amount);

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    // Allows shield penetrating items to disable shields and deal damage through them
    @ModifyExpressionValue(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z")
    )
    public boolean klaxon$shieldBreachingOverride(boolean original, @Local(argsOnly = true) DamageSource damageSource, @Local(argsOnly = true) float amount) {
        ItemStack weaponStack = damageSource.getWeaponStack();

        // make sure there's a weapon stack
        if (weaponStack != null && damageSource.getAttacker() instanceof LivingEntity attacker) {
            // make sure there is actually a shield penetration component
            ShieldBreachingComponent shieldBreachingComponent = ShieldBreachingComponent.get(weaponStack);
            if (shieldBreachingComponent != null) {
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
        }

        // no need to retain the original since any positives are filtered out at the start
        return original;
    }

    @ModifyReturnValue(
            method = "createLivingAttributes",
            at = @At(value = "RETURN")
    )
    private static DefaultAttributeContainer.Builder klaxon$appendKlaxonLivingAttributes(DefaultAttributeContainer.Builder original) {
        for (RegistryEntry<EntityAttribute> attribute : KlaxonEntityAttributes.getKlaxonGenericLivingEntityAttributes()) {
            original.add(attribute);
        }
        return original;
    }
}