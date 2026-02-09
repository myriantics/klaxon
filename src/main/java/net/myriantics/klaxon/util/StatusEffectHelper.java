package net.myriantics.klaxon.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import java.util.Collection;

public abstract class StatusEffectHelper {

    // FYI - getAmplifier() counts up from 0; returning 1 would indicate a level II effect
    public static int getUnborkedStatusEffectAmplifier(LivingEntity livingEntity, RegistryEntry<StatusEffect> statusEffect) {
        if (livingEntity != null && livingEntity.getStatusEffect(statusEffect) != null) {
            int amplifier = livingEntity.getStatusEffect(statusEffect).getAmplifier();

            return amplifier + 1;
        }
        return 0;
    }

    public static int totalLevelOfTagContents(Collection<StatusEffectInstance> effects, TagKey<StatusEffect> tagKey) {
        int cumulativeLevel = 0;
        for (StatusEffectInstance instance : effects) {
            if (instance.getEffectType().isIn(tagKey)) {
                cumulativeLevel += instance.getAmplifier() + 1;
            }
        }
        return cumulativeLevel;
    }

    public static boolean containsAnyEffectIn(Collection<StatusEffectInstance> effects, TagKey<StatusEffect> tagKey) {
        for (StatusEffectInstance effectInstance : effects) {
            if (effectInstance.getEffectType().isIn(tagKey)) return true;
        }
        return false;
    }
}
