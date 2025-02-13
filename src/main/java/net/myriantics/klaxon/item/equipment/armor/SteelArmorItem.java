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

public class SteelArmorItem extends ArmorItem {

    public static float JUMP_STRENGTH_MULTIPLIER = -0.12f;
    public static float MOVEMENT_SPEED_MULTIPLIER = -0.08f;
    public static float FALL_DAMAGE_MULTIPLER = 0.08f;

    public static final Identifier JUMP_STRENGTH_MULTIPLIER_ID = KlaxonCommon.locate("steel_armor_jump_strength_multiplier_id");
    public static final Identifier MOVEMENT_SPEED_MULTIPLIER_ID = KlaxonCommon.locate("steel_armor_movement_speed_multiplier_id");
    public static final Identifier FALL_DAMAGE_MULTIPLIER_ID = KlaxonCommon.locate("steel_armor_fall_damage_multiplier_id");

    public SteelArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings.maxDamage(type.getMaxDamage(15)).maxCount(1));
    }

    public static AttributeModifiersComponent appendAttributeModifiers(AttributeModifiersComponent attributeModifiers, AttributeModifierSlot slot) {

        return attributeModifiers
                .with(
                        EntityAttributes.GENERIC_JUMP_STRENGTH,
                        new EntityAttributeModifier(JUMP_STRENGTH_MULTIPLIER_ID, JUMP_STRENGTH_MULTIPLIER, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        slot
                ).with(
                        EntityAttributes.GENERIC_MOVEMENT_SPEED,
                        new EntityAttributeModifier(MOVEMENT_SPEED_MULTIPLIER_ID, MOVEMENT_SPEED_MULTIPLIER, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        slot
                ).with(
                        EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER,
                        new EntityAttributeModifier(FALL_DAMAGE_MULTIPLIER_ID, FALL_DAMAGE_MULTIPLER, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        slot
                );
    }
}
