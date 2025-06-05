package net.myriantics.klaxon.item.equipment.armor;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.custom.KlaxonAttributeModifierComponentModifications;
import net.myriantics.klaxon.registry.minecraft.KlaxonArmorMaterials;
import net.myriantics.klaxon.registry.minecraft.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.minecraft.KlaxonToolMaterials;

public class SteelArmorItem extends ArmorItem {

    public static final float WEIGHT_MODIFIER = 0.5f;
    public static final float MOVEMENT_SPEED_MULTIPLIER = -0.04f;

    public static final Identifier MOVEMENT_SPEED_MULTIPLIER_ID = KlaxonCommon.locate("steel_armor_movement_speed_multiplier_id");
    public static final Identifier WEIGHT_MODIFIER_ID = KlaxonCommon.locate("steel_armor_weight_modifier_id");

    public SteelArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings.maxDamage(type.getMaxDamage(32)).maxCount(1));
    }

    public static AttributeModifiersComponent appendAttributeModifiers(AttributeModifiersComponent attributeModifiers, AttributeModifierSlot slot) {

        return attributeModifiers
                .with(
                        EntityAttributes.GENERIC_MOVEMENT_SPEED,
                        new EntityAttributeModifier(KlaxonAttributeModifierComponentModifications.idFromSlot(MOVEMENT_SPEED_MULTIPLIER_ID, slot), MOVEMENT_SPEED_MULTIPLIER, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        slot
                                )
                .with(
                        KlaxonEntityAttributes.GENERIC_WEIGHT,
                        new EntityAttributeModifier(KlaxonAttributeModifierComponentModifications.idFromSlot(WEIGHT_MODIFIER_ID, slot), WEIGHT_MODIFIER, EntityAttributeModifier.Operation.ADD_VALUE),
                        slot
                );
    }
}
