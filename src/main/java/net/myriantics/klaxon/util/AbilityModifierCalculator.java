package net.myriantics.klaxon.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class AbilityModifierCalculator {
    /**
     * idk this makes pvp with stuff like the hammer funky -
     * weakness arrows go brrt
     *
     * @param
     * sourceEntity - Entity that is performing the walljump
     * @param
     * movedEntity - Entity that is moved by walljump
     * @return
     * Returns the multiplier that the hammer walljump should be multiplied by - factors in strength, weakness, and entity weight (Source and moved entity weights are combined).
     * Is always greater than 0.
     */
    public static float calculateHammerWalljumpMultiplier(LivingEntity sourceEntity, Entity movedEntity) {
        // status effect modifier starts out at 0 - tug-of-war between strength and weakness begins
        int statusEffectModifier = 0;

        // TIL weakness doesn't have a tier 2 version. The more you know
        statusEffectModifier += StatusEffectHelper.getUnborkedStatusEffectAmplifier(sourceEntity, StatusEffects.STRENGTH);
        statusEffectModifier -= StatusEffectHelper.getUnborkedStatusEffectAmplifier(sourceEntity, StatusEffects.WEAKNESS);

        // factor in entity weight value - defined by attribute modifier
        // its divided by half so that you can offset wearing full steel armor by having strength 2 in vanilla klaxon
        // starts out at 1 - you cannot walljump if you have the heavy effect at all

        double weightValue = EntityWeightHelper.getEntityWeightValue(sourceEntity);
        if (movedEntity != null && !movedEntity.equals(sourceEntity)) weightValue += EntityWeightHelper.getEntityWeightValue(movedEntity);

        // compile all the factors
        float totalModifier = (float) (statusEffectModifier - weightValue);

        // make it not crazy powerful
        if (totalModifier > 0) {
            totalModifier *= 0.2f;
        }

        // KlaxonCommon.LOGGER.info("Total Ability Modifier: "  + Math.max(0, 1 + totalModifier));

        // ensure it doesn't cause negative velocity
        return Math.max(0, 1 + totalModifier);
    }
}
