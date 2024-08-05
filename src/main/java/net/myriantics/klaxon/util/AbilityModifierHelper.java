package net.myriantics.klaxon.util;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public abstract class AbilityModifierHelper {
    public static float calculate(PlayerEntity player) {

        // idk this makes pvp with stuff like the hammer funky
        // weakness arrows go brrt
        // 0.5 is way too spicy, lets try 0.2 instead
        float strengthAbilityModifier = 1.0F;
        if (player.getStatusEffect(StatusEffects.STRENGTH) != null) {
            strengthAbilityModifier += (0.2F * player.getStatusEffect(StatusEffects.STRENGTH).getAmplifier());
        }

        // TIL weakness doesn't have a tier 2 version. The more you know
        if (player.getStatusEffect(StatusEffects.WEAKNESS) != null) {
            strengthAbilityModifier -= (player.getStatusEffect(StatusEffects.WEAKNESS).getAmplifier());
        }

        if (strengthAbilityModifier <= 0) {
            return 0.0F;
        }

        return strengthAbilityModifier;
    }
}
