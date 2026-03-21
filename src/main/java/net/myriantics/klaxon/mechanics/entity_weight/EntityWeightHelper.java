package net.myriantics.klaxon.mechanics.entity_weight;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;
import net.myriantics.klaxon.util.StatusEffectHelper;

public abstract class EntityWeightHelper {
    public static boolean isHeavy(Entity entity) {
        EntityType<? extends Entity> type = entity.getType();

        // mobs with no AI are heavy because they can't move
        if (entity instanceof Mob mobEntity && mobEntity.isNoAi()) {
            return true;
        }

        // The light_entities tag cancels out all weight modifiers - entity is always light
        if (type.is(KlaxonEntityTypeTags.LIGHT_ENTITIES)) {
            return false;
        }

        // The heavy_entities tag cancels out all weight modifiers save for light_entities - entity is almost always heavy
        if (type.is(KlaxonEntityTypeTags.HEAVY_ENTITIES)) {
            return true;
        }

        // fallback calculations in order of priciness

        // entities larger than a boat or wearing any heavy equipment are considered heavy
        if (entity.getBbWidth() > EntityType.BOAT.getWidth()) {
            return true;
        }

        // if an entity has implemented more advanced behavior, check that
        if (entity instanceof AdvancedEntityWeightBehavior behavior) {
            if (behavior.klaxon$isHeavy()) {
                return true;
            }
        }

        // the next conditions only apply to living entities so check for that
        if (entity instanceof LivingEntity livingEntity) {
            // entities with the heavy equipment attachment are considered heavy
            if (isEntityWearingHeavyEquipment(livingEntity)) {
                return true;
            }

            // living entities with any status effect in the heavy tag are considered heavy
            return StatusEffectHelper.containsAnyEffectIn(livingEntity.getActiveEffects(), KlaxonStatusEffectTags.HEAVY_EFFECTS);
        }

        // return the fallback value
        return false;
    }

    private static boolean isEntityWearingHeavyEquipment(LivingEntity livingEntity) {
        for (ItemStack stack : livingEntity.getArmorAndBodyArmorSlots()) {
            if (stack.is(KlaxonItemTags.HEAVY_EQUIPMENT)) {
                return true;
            }
        }
        return false;
    }
}
