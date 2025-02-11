package net.myriantics.klaxon.item.equipment.armor;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;

public class SteelArmorItem extends ArmorItem {

    public static float JUMP_STRENGTH_MULTIPLIER = 0.8f;

    public SteelArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
    }

    public static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_JUMP_STRENGTH,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, JUMP_STRENGTH_MULTIPLIER, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        AttributeModifierSlot.ARMOR
                ).build();
    }
}
