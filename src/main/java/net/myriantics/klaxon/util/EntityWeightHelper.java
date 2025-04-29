package net.myriantics.klaxon.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.myriantics.klaxon.registry.minecraft.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.minecraft.KlaxonStatusEffects;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class EntityWeightHelper {

    public static boolean isHeavy(LivingEntity entity) {
        return getEntityWeightValue(entity) > 1;
    }

    /**
     * Entities have a default weight value of 0.0 - this can be modified by wearing Steel or Netherite Armor as well as with the Heavy status effect
     **/
    public static double getEntityWeightValue(LivingEntity entity) {
        return entity.getAttributeValue(KlaxonEntityAttributes.GENERIC_WEIGHT);
    }

    public static boolean isStackHeavy(ItemStack stack) {
        return stack.isIn(KlaxonItemTags.HEAVY_EQUIPMENT);
    }
}
