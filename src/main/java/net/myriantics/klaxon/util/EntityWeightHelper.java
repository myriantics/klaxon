package net.myriantics.klaxon.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.registry.entity.KlaxonDataAttachments;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;

public abstract class EntityWeightHelper {
    public static boolean isHeavy(Entity entity) {
        EntityType<? extends Entity> type = entity.getType();

        // The light_entities tag cancels out all weight modifiers - entity is always light
        if (type.isIn(KlaxonEntityTypeTags.LIGHT_ENTITIES)) {
            return false;
        }

        // The heavy_entities tag cancels out all weight modifiers save for light_entities - entity is almost always heavy
        if (type.isIn(KlaxonEntityTypeTags.HEAVY_ENTITIES)) {
            return true;
        }

        // fallback calculations in order of priciness

        // entities larger than a boat or wearing any heavy equipment are considered heavy
        boolean heavy = entity.getWidth() >= EntityType.BOAT.getWidth();

        // entities with the heavy equipment attachment are considered heavy
        if (!heavy) {
            heavy = Boolean.TRUE.equals(entity.getAttached(KlaxonDataAttachments.HEAVY_EQUIPMENT));
        }

        // living entities with any status effect in the heavy tag are considered heavy
        if (!heavy && entity instanceof LivingEntity livingEntity) {
            heavy = StatusEffectHelper.containsAnyEffectIn(livingEntity.getStatusEffects(), KlaxonStatusEffectTags.HEAVY_EFFECTS);
        }

        // return the fallback value
        return heavy;
    }
}
