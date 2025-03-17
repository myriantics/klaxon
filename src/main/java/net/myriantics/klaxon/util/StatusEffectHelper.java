package net.myriantics.klaxon.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public abstract class StatusEffectHelper {

    // FYI - getAmplifier() counts up from 0; returning 1 would indicate a level II effect
    public static int getUnborkedStatusEffectAmplifier(LivingEntity livingEntity, StatusEffect statusEffect) {
        if (livingEntity != null && livingEntity.getStatusEffect(statusEffect) != null) {
            int amplifier = livingEntity.getStatusEffect(statusEffect).getAmplifier();

            return amplifier + 1;
        }
        return 0;
    }

    public static void recomputePersistentEffects(LivingEntity livingEntity) {
        EntityWeightHelper.updateEntityWeightStatusEffect(livingEntity, null, null);
    }

}
