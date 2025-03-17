package net.myriantics.klaxon.item.equipment.armor;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class KlaxonArmorMaterials{
    public static final ArmorMaterial STEEL = register("steel", 27, Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
    }), 0, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.5f, 0.1f, () -> Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES));

    private static ArmorMaterial register(
            String id,
            int durabilityMultiplier,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantability,
            SoundEvent equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient
    ) {
        return new ArmorMaterial() {

            @Override
            public int getDurability(ArmorItem.Type type) {
                return BASE_DURABILITY.get(type) * durabilityMultiplier;
            }

            @Override
            public int getProtection(ArmorItem.Type type) {
                return defense.get(type);
            }

            @Override
            public int getEnchantability() {
                return enchantability;
            }

            @Override
            public SoundEvent getEquipSound() {
                return equipSound;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return repairIngredient.get();
            }

            @Override
            public String getName() {
                return id;
            }

            @Override
            public float getToughness() {
                return toughness;
            }

            @Override
            public float getKnockbackResistance() {
                return knockbackResistance;
            }
        };
    }

    private static final EnumMap<ArmorItem.Type, Integer> BASE_DURABILITY = Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 13);
        map.put(ArmorItem.Type.LEGGINGS, 15);
        map.put(ArmorItem.Type.CHESTPLATE, 16);
        map.put(ArmorItem.Type.HELMET, 11);
    });

    public static void registerArmorMaterials() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Armor Materials");
    }
}
