package net.myriantics.klaxon.util;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public abstract class AbilityModifierCalculator {
    public static float calculateHammerWalljumpMultiplier(PlayerEntity player) {

        // idk this makes pvp with stuff like the hammer funky
        // weakness arrows go brrt
        float statusEffectModifier = 0.0F;

        // TIL weakness doesn't have a tier 2 version. The more you know
        statusEffectModifier += StatusEffectHelper.getUnborkedStatusEffectAmplifier(player, StatusEffects.STRENGTH);
        statusEffectModifier -= StatusEffectHelper.getUnborkedStatusEffectAmplifier(player, StatusEffects.WEAKNESS);

        // factor in heaviness potion effects - defined by tag
        // its divided by half so that you can offset wearing full steel armor by having strength 2 in vanilla klaxon
        // starts out at 1 - you cannot walljump if you have the heavy effect at all

        int weightValue = EntityWeightHelper.getEntityWeightValue(player);
        int augmentedWeightValue = (weightValue > 0) ? Math.max(1, weightValue) : 0;

        statusEffectModifier -= augmentedWeightValue * 0.5f;


        // no negative velocity for you - any negative value results in no walljump
        if (statusEffectModifier < 0) {
            return 0.0F;
        }

        // make it not crazy powerful
        statusEffectModifier *= 0.2f;

        // make it not a debuff by adding 1
        statusEffectModifier++;

        return statusEffectModifier;
    }
}
