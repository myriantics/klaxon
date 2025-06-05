package net.myriantics.klaxon.registry.custom;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.armor.SteelArmorItem;
import net.myriantics.klaxon.registry.minecraft.KlaxonArmorMaterials;
import net.myriantics.klaxon.registry.minecraft.KlaxonEntityAttributes;

public class KlaxonAttributeModifierComponentModifications {

    public static AttributeModifiersComponent applyArmorModifications(ArmorItem armorItem, AttributeModifiersComponent original) {
        RegistryEntry<ArmorMaterial> material = armorItem.getMaterial();
        EquipmentSlot slot = armorItem.getSlotType();

        // apply steel armor modifiers if it's steel armor
        if (material.equals(KlaxonArmorMaterials.STEEL_PLATE)) {
            original = SteelArmorItem.appendAttributeModifiers(original, AttributeModifierSlot.forEquipmentSlot(slot));
        }

        // apply netherite armor modifiers if it's netherite armor
        if (material.equals(ArmorMaterials.NETHERITE)) {
            original = applyNetheriteArmorComponentModifications(original, AttributeModifierSlot.forEquipmentSlot(slot));
        }

        return original;
    }

    private static AttributeModifiersComponent applyNetheriteArmorComponentModifications(AttributeModifiersComponent component, AttributeModifierSlot slot) {
        return component.with(
                KlaxonEntityAttributes.GENERIC_WEIGHT,
                new EntityAttributeModifier(idFromSlot(KlaxonCommon.locate("netherite_armor_weight_modifier_id"), slot), 0.25f, EntityAttributeModifier.Operation.ADD_VALUE),
                slot
        );
    }

    public static Identifier idFromSlot(Identifier id, AttributeModifierSlot slot) {
        return id.withPath(id.getPath() + "_" + slot.asString().toLowerCase());
    }
}
