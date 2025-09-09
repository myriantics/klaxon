package net.myriantics.klaxon.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.registry.entity.KlaxonDataAttachments;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
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
            for (StatusEffectInstance instance : livingEntity.getStatusEffects()) {
                if (instance.getEffectType().isIn(KlaxonStatusEffectTags.HEAVY_STATUS_EFFECTS)) {
                    heavy = true;
                    break;
                }
            }
        }

        // return the fallback value
        return heavy;
    }
}
