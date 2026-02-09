package net.myriantics.klaxon.registry.item;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.armor.SteelArmorItem;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;

public abstract class KlaxonAttributeModifierComponentModifications {

    public static AttributeModifiersComponent applyArmorModifications(ArmorItem armorItem, AttributeModifiersComponent original) {
        RegistryEntry<ArmorMaterial> material = armorItem.getMaterial();
        EquipmentSlot slot = armorItem.getSlotType();

        // apply steel armor modifiers if it's steel armor
        if (material.equals(KlaxonArmorMaterials.STEEL_PLATE)) {
            original = SteelArmorItem.appendAttributeModifiers(original, AttributeModifierSlot.forEquipmentSlot(slot));
        }

        return original;
    }

    public static Identifier idFromSlot(Identifier id, AttributeModifierSlot slot) {
        return id.withPath(id.getPath() + "_" + slot.asString().toLowerCase());
    }
}
