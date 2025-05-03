package net.myriantics.klaxon.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.minecraft.KlaxonEntityAttributes;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public abstract class EntityWeightHelper {
    public static boolean isHeavy(LivingEntity entity) {
        return getEntityWeightValue(entity) > 1;
    }

    /**
     * Used to obtain any given entity's Weight value. Used in calculations determining equipment functionality,
     * this stat can be modified via attribute modifier, size modifier, or specific tags
     * @param entity Entity that shall be polled for its weight value.
     * @return Returns a value of either -1 or any value between 0 & 20.
     * Integer.MAX_VALUE indicates ultra heavy; 0 indicates no weight; any other value indicates some weight.
     **/
    public static double getEntityWeightValue(Entity entity) {
        if (entity.getType().isIn(KlaxonEntityTypeTags.ULTRA_LIGHT_ENTITIES)) return 0.0;
        if (entity.getType().isIn(KlaxonEntityTypeTags.ULTRA_HEAVY_ENTITIES)) return Integer.MAX_VALUE;

        // Initial weight value is obtained from entity dimensions
        double weight = calculateEntityWeightFromDimensions(entity);

        KlaxonCommon.LOGGER.info("Targeted " + entity.getName() + " has dimension weight value of: " + weight);

        // If a compatible Entity is provided, attribute modifiers are appended.
        if (entity instanceof LivingEntity livingEntity) {
            weight += livingEntity.getAttributeValue(KlaxonEntityAttributes.GENERIC_WEIGHT);
        }

        // Adjust values according to tags. Priority is Light > Heavy.
        if (entity.getType().isIn(KlaxonEntityTypeTags.LIGHT_ENTITIES)) {
            weight -= 1;
        } else if (entity.getType().isIn(KlaxonEntityTypeTags.HEAVY_ENTITIES)) {
            weight += 1;
        }

        return Math.clamp(weight, 0, 20);
    }

    /**
     * Resultant weight values are based on the entity's longest side - sides under 2 blocks in length are ignored,
     * and sides above that value have the resultant weight decremented by 1.
     * @param entity The entity to calculate the weight of
     * @return The resultant weight value from the entity. Can be 0, or any number greater than 1.
     */
    public static double calculateEntityWeightFromDimensions(Entity entity) {
        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        double longestSide = Math.max(dimensions.width(), dimensions.height());

        if (longestSide > 2) {
            return longestSide - 1;
        }

        return 0.0;
    }

    public static boolean isStackHeavy(ItemStack stack) {
        return stack.isIn(KlaxonItemTags.HEAVY_EQUIPMENT);
    }
}
