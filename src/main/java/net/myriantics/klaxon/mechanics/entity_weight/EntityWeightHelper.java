package net.myriantics.klaxon.mechanics.entity_weight;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;
import net.myriantics.klaxon.util.StatusEffectHelper;

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
        if (entity.getWidth() > EntityType.BOAT.getWidth()) {
            return true;
        }

        // Falling blocks are considered heavy if they block piston movement. This includes anvils by default.
        if (entity instanceof FallingBlockEntity fallingBlockEntity) {
            if (fallingBlockEntity.getBlockState().getPistonBehavior().equals(PistonBehavior.BLOCK)) {
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
            return StatusEffectHelper.containsAnyEffectIn(livingEntity.getStatusEffects(), KlaxonStatusEffectTags.HEAVY_EFFECTS);
        }

        // return the fallback value
        return false;
    }

    private static boolean isEntityWearingHeavyEquipment(LivingEntity livingEntity) {
        for (ItemStack stack : livingEntity.getAllArmorItems()) {
            if (stack.isIn(KlaxonItemTags.HEAVY_EQUIPMENT)) {
                return true;
            }
        }
        return false;
    }
}
