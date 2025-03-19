package net.myriantics.klaxon.item.equipment.armor;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.EnumMap;
import java.util.UUID;

public class SteelArmorItem extends ArmorItem {

    public static float JUMP_STRENGTH_MULTIPLIER = -0.12f;
    public static float MOVEMENT_SPEED_MULTIPLIER = -0.08f;
    public static float FALL_DAMAGE_MULTIPLER = 0.08f;

    public static final Identifier JUMP_STRENGTH_MULTIPLIER_ID = KlaxonCommon.locate("steel_armor_jump_strength_multiplier_id");
    public static final Identifier MOVEMENT_SPEED_MULTIPLIER_ID = KlaxonCommon.locate("steel_armor_movement_speed_multiplier_id");
    public static final Identifier FALL_DAMAGE_MULTIPLIER_ID = KlaxonCommon.locate("steel_armor_fall_damage_multiplier_id");
    public static final Identifier KNOCKBACK_RESISTANCE_MULTIPLIER_ID = KlaxonCommon.locate("steel_armor_knockback_resistance_multiplier_id");

    public SteelArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings.maxDamage(material.getDurability(type)));
    }

    public static void appendAttributeModifiers(ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder, EnumMap<Type, UUID> modifiers, ArmorMaterial material, ArmorItem.Type type) {

        builder.put(
                EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                new EntityAttributeModifier(modifiers.get(type), KNOCKBACK_RESISTANCE_MULTIPLIER_ID.getPath(), material.getKnockbackResistance(), EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                new EntityAttributeModifier(modifiers.get(type), MOVEMENT_SPEED_MULTIPLIER_ID.getPath(), MOVEMENT_SPEED_MULTIPLIER, EntityAttributeModifier.Operation.MULTIPLY_BASE)
        );
    }
}
