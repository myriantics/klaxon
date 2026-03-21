package net.myriantics.klaxon.registry.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.myriantics.klaxon.item.equipment.armor.SteelArmorItem;

public abstract class KlaxonAttributeModifierComponentModifications {

    public static ItemAttributeModifiers applyArmorModifications(ArmorItem armorItem, ItemAttributeModifiers original) {
        Holder<ArmorMaterial> material = armorItem.getMaterial();
        EquipmentSlot slot = armorItem.getEquipmentSlot();

        // apply steel armor modifiers if it's steel armor
        if (material.equals(KlaxonArmorMaterials.STEEL_PLATE)) {
            original = SteelArmorItem.appendAttributeModifiers(original, EquipmentSlotGroup.bySlot(slot));
        }

        return original;
    }

    public static ResourceLocation idFromSlot(ResourceLocation id, EquipmentSlotGroup slot) {
        return id.withPath(id.getPath() + "_" + slot.getSerializedName().toLowerCase());
    }
}
