package net.myriantics.klaxon.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public abstract class AbilityModifierHelper {
    public static float calculate(PlayerEntity player) {

        // idk this makes pvp with stuff like the hammer funky
        // weakness arrows go brrt
        float statusEffectModifier = 0.0F;


        // TIL weakness doesn't have a tier 2 version. The more you know
        statusEffectModifier += getUnborkedStatusEffectAmplifier(player, StatusEffects.STRENGTH);
        statusEffectModifier -= getUnborkedStatusEffectAmplifier(player, StatusEffects.WEAKNESS);

        // no negative velocity for you
        if (statusEffectModifier < 0) {
            return 0.0F;
        }

        // make it not crazy powerful
        statusEffectModifier *= 0.2F;
        // make it not a debuff by adding 1
        statusEffectModifier++;

        player.sendMessage(Text.literal("test: " + statusEffectModifier));


        return statusEffectModifier;
    }

    // FYI - getAmplifier() counts up from 0; returning 1 would indicate a level II effect
    private static float getUnborkedStatusEffectAmplifier(PlayerEntity player, StatusEffect statusEffect) {
        if (player != null && player.getStatusEffect(statusEffect) != null) {
            float amplifier = (float) player.getStatusEffect(statusEffect).getAmplifier();

            return amplifier + 1;
        }
        return 0.0F;
    }
}
