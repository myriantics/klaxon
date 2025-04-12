package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.ability.ShieldPenetrationComponent;
import net.myriantics.klaxon.component.configuration.DamageTypeOverrideComponent;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.component.configuration.RepairIngredientOverrideComponent;
import net.myriantics.klaxon.item.equipment.armor.KlaxonArmorMaterials;
import net.myriantics.klaxon.item.equipment.armor.SteelArmorItem;
import net.myriantics.klaxon.item.equipment.tools.CleaverItem;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.item.equipment.tools.CableShearsItem;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.ArrayList;

public class KlaxonItems {

    public static ArrayList<Item> simpleItems = new ArrayList<>();

    // tools
    public static final Item STEEL_HAMMER = registerSimpleItem("steel_hammer",
            new HammerItem(KlaxonToolMaterials.STEEL, new Item.Settings()
                    .attributeModifiers(HammerItem.createAttributeModifiers(KlaxonToolMaterials.STEEL, HammerItem.STEEL_HAMMER_BASE_ATTACK_DAMAGE, HammerItem.STEEL_HAMMER_ATTACK_SPEED))
                    .component(KlaxonDataComponentTypes.WALLJUMP_ABILITY, new WalljumpAbilityComponent(1.0f, true))
                    .component(KlaxonDataComponentTypes.DAMAGE_TYPE_OVERRIDE, new DamageTypeOverrideComponent(KlaxonDamageTypes.HAMMER_BONKING))
                    .component(KlaxonDataComponentTypes.SHIELD_PENETRATION, new ShieldPenetrationComponent(KlaxonDamageTypes.HAMMER_WALLOPING, true))));
    public static final Item STEEL_CABLE_SHEARS = registerSimpleItem("steel_cable_shears",
            new CableShearsItem(KlaxonToolMaterials.STEEL, new Item.Settings()
                    .component(KlaxonDataComponentTypes.REPAIR_INGREDIENT_OVERRIDE, new RepairIngredientOverrideComponent(Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES)))
                    .attributeModifiers(CableShearsItem.createAttributeModifiers())
            ));
    public static final Item STEEL_CLEAVER = registerSimpleItem("steel_cleaver",
            new CleaverItem(KlaxonToolMaterials.STEEL, new Item.Settings()
                    .component(KlaxonDataComponentTypes.REPAIR_INGREDIENT_OVERRIDE, new RepairIngredientOverrideComponent(Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES)))
            ));
    public static final Item STEEL_WRENCH = registerItem("steel_wrench",
            new WrenchItem(KlaxonToolMaterials.STEEL, new Item.Settings()
                    .component(KlaxonDataComponentTypes.REPAIR_INGREDIENT_OVERRIDE, new RepairIngredientOverrideComponent(Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES)))
            ));

    // armor
    public static final Item STEEL_HELMET = registerSimpleItem("steel_helmet",
            new SteelArmorItem(KlaxonArmorMaterials.STEEL, ArmorItem.Type.HELMET, new Item.Settings()));
    public static final Item STEEL_CHESTPLATE = registerSimpleItem("steel_chestplate",
            new SteelArmorItem(KlaxonArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final Item STEEL_LEGGINGS = registerSimpleItem("steel_leggings",
            new SteelArmorItem(KlaxonArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final Item STEEL_BOOTS = registerSimpleItem("steel_boots",
            new SteelArmorItem(KlaxonArmorMaterials.STEEL, ArmorItem.Type.BOOTS, new Item.Settings()));

    // raw ores / blends
    public static final Item FRACTURED_RAW_IRON = registerReallySimpleItem("fractured_raw_iron");
    public static final Item FRACTURED_RAW_COPPER = registerReallySimpleItem("fractured_raw_copper");
    public static final Item FRACTURED_RAW_GOLD = registerReallySimpleItem("fractured_raw_gold");
    public static final Item FRACTURED_IRON_FRAGMENTS = registerReallySimpleItem("fractured_iron_fragments");
    public static final Item FRACTURED_COPPER_FRAGMENTS = registerReallySimpleItem("fractured_copper_fragments");
    public static final Item FRACTURED_GOLD_FRAGMENTS = registerReallySimpleItem("fractured_gold_fragments");
    public static final Item FRACTURED_COAL_CHUNKS = registerReallySimpleItem("fractured_coal_chunks");
    public static final Item CRUDE_STEEL_MIXTURE = registerReallySimpleItem("crude_steel_mixture");

    // raw materials
    public static final Item STEEL_INGOT = registerReallySimpleItem("steel_ingot");
    public static final Item STEEL_NUGGET = registerReallySimpleItem("steel_nugget");
    public static final Item CRUDE_STEEL_INGOT = registerReallySimpleItem("crude_steel_ingot");
    public static final Item CRUDE_STEEL_NUGGET = registerReallySimpleItem("crude_steel_nugget");
    public static final Item RUBBER_GLOB = registerReallySimpleItem("rubber_glob");
    public static final Item MOLTEN_RUBBER_GLOB = registerReallySimpleItem("molten_rubber_glob");

    // plates / sheets
    public static final Item STEEL_PLATE = registerReallySimpleItem("steel_plate");
    public static final Item CRUDE_STEEL_PLATE = registerReallySimpleItem("crude_steel_plate");
    public static final Item IRON_PLATE = registerReallySimpleItem("iron_plate");
    public static final Item GOLD_PLATE = registerReallySimpleItem("gold_plate");
    public static final Item COPPER_PLATE = registerReallySimpleItem("copper_plate");
    public static final Item RUBBER_SHEET = registerReallySimpleItem("rubber_sheet");
    public static final Item MOLTEN_RUBBER_SHEET = registerReallySimpleItem("molten_rubber_sheet");

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name), item);
    }

    private static Item registerReallySimpleItem(String name) {
        return registerSimpleItem(name, new Item(new Item.Settings()));
    }

    private static Item registerSimpleItem(String name, Item item) {
        simpleItems.add(item);
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name), item);
    }

    public static void registerModItems() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Items!");
    }
}
