package net.myriantics.klaxon.util;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;

public abstract class StatusEffectHelper {

    // FYI - getAmplifier() counts up from 0; returning 1 would indicate a level II effect
    public static int getUnborkedStatusEffectAmplifier(LivingEntity livingEntity, Holder<MobEffect> statusEffect) {
        if (livingEntity != null && livingEntity.getEffect(statusEffect) != null) {
            int amplifier = livingEntity.getEffect(statusEffect).getAmplifier();

            return amplifier + 1;
        }
        return 0;
    }

    public static int totalLevelOfTagContents(Collection<MobEffectInstance> effects, TagKey<MobEffect> tagKey) {
        int cumulativeLevel = 0;
        for (MobEffectInstance instance : effects) {
            if (instance.getEffect().is(tagKey)) {
                cumulativeLevel += instance.getAmplifier() + 1;
            }
        }
        return cumulativeLevel;
    }

    public static boolean containsAnyEffectIn(Collection<MobEffectInstance> effects, TagKey<MobEffect> tagKey) {
        for (MobEffectInstance effectInstance : effects) {
            if (effectInstance.getEffect().is(tagKey)) return true;
        }
        return false;
    }
}
