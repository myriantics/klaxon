package net.myriantics.klaxon.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;

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

        // Sum up all strengthening and weakening effects and modify the modifier
        statusEffectModifier += StatusEffectHelper.totalLevelOfTagContents(sourceEntity.getStatusEffects(), KlaxonStatusEffectTags.STRENGTHENING_EFFECTS);
        statusEffectModifier -= StatusEffectHelper.totalLevelOfTagContents(sourceEntity.getStatusEffects(), KlaxonStatusEffectTags.WEAKENING_EFFECTS);

        // if either source or moved entity is heavy, walljump strength is halved
        boolean heavy = EntityWeightHelper.isHeavy(sourceEntity) || EntityWeightHelper.isHeavy(movedEntity);

        // compile all the factors
        float totalModifier;
        if (heavy && statusEffectModifier < 0) {
            // heavy while having weakness? believe it or not, straight to 0
            totalModifier = 0;
        } else if (heavy && statusEffectModifier > 0) {
            // heavy while having strength? believe it or not, start at 1 and halve effectiveness of strength
            totalModifier = 1f + (0.1f * statusEffectModifier);
        } else {
            // total modifier is 0.5 if heavy
            totalModifier = heavy ? 0.5f : 1f;
            // weakness is stronger than strength - do that to total modifier
            // these are great comments btw
            totalModifier += statusEffectModifier * (statusEffectModifier < 0 ? 0.5f : 0.2f);
        }

        // ensure it doesn't cause negative velocity
        return Math.max(0, totalModifier);
    }
}
