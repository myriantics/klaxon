package net.myriantics.klaxon.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.myriantics.klaxon.registry.KlaxonStatusEffects;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public abstract class EntityWeightHelper {

    public static boolean isHeavy(LivingEntity entity) {
        return getEntityWeightValue(entity) > 0;
    }

    public static int getEntityWeightValue(LivingEntity entity) {
        int weightValue = 0;

        Optional<RegistryEntryList.Named<StatusEffect>> effectsToCheck = Registries.STATUS_EFFECT.getEntryList(KlaxonStatusEffectTags.HEAVY_STATUS_EFFECTS);

        // deal with the optional so intellij doesnt complain
        if (effectsToCheck.isEmpty()) return 0;

        // go through all the effect values in the tag. if the entity has any of them, add their amplifier to the weight value.
        for (RegistryEntry<StatusEffect> effect : effectsToCheck.get()) {
            weightValue += StatusEffectHelper.getUnborkedStatusEffectAmplifier(entity, effect.value());
        }

        return weightValue;
    }

    // called in LivingEntityMixin
    public static void updateEntityWeightStatusEffect(LivingEntity entity, Map<EquipmentSlot, ItemStack> changedSlots) {
        int heavyStatusEffectNewValue = 0;

        for (EquipmentSlot checkedSlot : EquipmentSlot.values()) {
            if (checkedSlot.isArmorSlot()) {
                boolean checkedEqualsChanged = changedSlots.containsKey(checkedSlot);

                // ternary bs here to prevent desync bs when doffing armor
                ItemStack stack = checkedEqualsChanged && changedSlots.get(checkedSlot) != null ? changedSlots.get(checkedSlot) : entity.getEquippedStack(checkedSlot);

                // add one to the tally for every piece of heavy equipment equipped
                heavyStatusEffectNewValue += isStackHeavy(stack) ? 1 : 0;
            }
        }

        // if new value is 0, don't bother updating effect
        if (heavyStatusEffectNewValue == 0) {
            entity.removeStatusEffect(KlaxonStatusEffects.HEAVY.value());
        } else {
            // subtract 1 from new value because effects are 0-indexed
            heavyStatusEffectNewValue--;

            entity.setStatusEffect(new StatusEffectInstance(KlaxonStatusEffects.HEAVY.value(), -1, heavyStatusEffectNewValue, false, false), entity);
        }
    }

    public static boolean isStackHeavy(ItemStack stack) {
        return stack.isIn(KlaxonItemTags.HEAVY_EQUIPMENT);
    }
}
