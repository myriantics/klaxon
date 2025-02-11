package net.myriantics.klaxon.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.item.consumables.EnderPlateItem;
import net.myriantics.klaxon.item.equipment.armor.KlaxonArmorMaterials;
import net.myriantics.klaxon.item.equipment.armor.SteelArmorItem;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;

import java.util.ArrayList;

public class KlaxonItems {

    public static ArrayList<Item> simpleItems = new ArrayList<>();

    // disabled currently because its broken as FUCK - will come later trust
    public static final Item ENDER_PEARL_PLATE_ITEM = registerSimpleItem("ender_plate", new EnderPlateItem(new Item.Settings()));

    // equipment
    public static final Item STEEL_HAMMER = registerSimpleItem("steel_hammer", new HammerItem(new Item.Settings().maxCount(1).maxDamage(512).attributeModifiers(HammerItem.createAttributeModifiers())));
    public static final Item STEEL_HELMET = registerSimpleItem("steel_helmet", new SteelArmorItem(KlaxonArmorMaterials.STEEL, ArmorItem.Type.HELMET, new Item.Settings().attributeModifiers(SteelArmorItem.createAttributeModifiers()).maxCount(1)));
    public static final Item STEEL_CHESTPLATE = registerSimpleItem("steel_chestplate", new SteelArmorItem(KlaxonArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE, new Item.Settings().attributeModifiers(SteelArmorItem.createAttributeModifiers()).maxCount(1)));
    public static final Item STEEL_LEGGINGS = registerSimpleItem("steel_leggings", new SteelArmorItem(KlaxonArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS, new Item.Settings().attributeModifiers(SteelArmorItem.createAttributeModifiers()).maxCount(1)));
    public static final Item STEEL_BOOTS = registerSimpleItem("steel_boots", new SteelArmorItem(KlaxonArmorMaterials.STEEL, ArmorItem.Type.BOOTS, new Item.Settings().attributeModifiers(SteelArmorItem.createAttributeModifiers()).maxCount(1)));

    // raw ores / blends
    public static final Item FRACTURED_RAW_IRON = registerReallySimpleItem("fractured_raw_iron");
    public static final Item FRACTURED_RAW_COPPER = registerReallySimpleItem("fractured_raw_copper");
    public static final Item FRACTURED_RAW_GOLD = registerReallySimpleItem("fractured_raw_gold");
    public static final Item FRACTURED_IRON_FRAGMENTS = registerReallySimpleItem("fractured_iron_fragments");
    public static final Item FRACTURED_COPPER_FRAGMENTS = registerReallySimpleItem("fractured_copper_fragments");
    public static final Item FRACTURED_GOLD_FRAGMENTS = registerReallySimpleItem("fractured_gold_fragments");
    public static final Item CRUDE_STEEL_MIXTURE = registerReallySimpleItem("crude_steel_mixture");

    // raw materials
    public static final Item CRUDE_STEEL_INGOT = registerReallySimpleItem("crude_steel_ingot");
    public static final Item CRUDE_STEEL_NUGGET = registerReallySimpleItem("crude_steel_nugget");
    public static final Item STEEL_INGOT = registerReallySimpleItem("steel_ingot");
    public static final Item STEEL_NUGGET = registerReallySimpleItem("steel_nugget");

    // processed materials
    public static final Item CRUDE_STEEL_PLATE = registerReallySimpleItem("crude_steel_plate");
    public static final Item STEEL_PLATE = registerReallySimpleItem("steel_plate");
    public static final Item IRON_PLATE = registerReallySimpleItem("iron_plate");
    public static final Item GOLD_PLATE = registerReallySimpleItem("gold_plate");
    public static final Item COPPER_PLATE = registerReallySimpleItem("copper_plate");

    private static void addItemsToToolTabGroup(FabricItemGroupEntries entries) {
        entries.add(STEEL_HAMMER);
    }

    private static void addItemsToRedstoneTabGroup(FabricItemGroupEntries entries) {
        entries.add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
        entries.add(KlaxonItems.STEEL_HAMMER);
    }

    private static void addItemsToCombatTabGroup(FabricItemGroupEntries entries) {
        entries.add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
        entries.add(KlaxonItems.STEEL_HAMMER);
        entries.add(KlaxonItems.STEEL_HELMET);
        entries.add(KlaxonItems.STEEL_CHESTPLATE);
        entries.add(KlaxonItems.STEEL_LEGGINGS);
        entries.add(KlaxonItems.STEEL_BOOTS);
    }

    private static void addItemsToIngredientTabGroup(FabricItemGroupEntries entries) {
        entries.add(KlaxonItems.FRACTURED_RAW_IRON);
        entries.add(KlaxonItems.FRACTURED_RAW_COPPER);
        entries.add(KlaxonItems.FRACTURED_RAW_GOLD);
        entries.add(KlaxonItems.FRACTURED_IRON_FRAGMENTS);
        entries.add(KlaxonItems.FRACTURED_COPPER_FRAGMENTS);
        entries.add(KlaxonItems.FRACTURED_GOLD_FRAGMENTS);
        entries.add(KlaxonItems.CRUDE_STEEL_MIXTURE);
        entries.add(KlaxonItems.STEEL_INGOT);
        entries.add(KlaxonItems.STEEL_NUGGET);
        entries.add(KlaxonBlocks.STEEL_BLOCK);
    }

    private static void addItemsToBuildingBlocksTabGroup(FabricItemGroupEntries entries) {
        entries.add(KlaxonBlocks.STEEL_BLOCK);
    }

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
        KlaxonCommon.LOGGER.info("Registering KLAXON's Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(KlaxonItems::addItemsToToolTabGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(KlaxonItems::addItemsToRedstoneTabGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(KlaxonItems::addItemsToCombatTabGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(KlaxonItems::addItemsToIngredientTabGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(KlaxonItems::addItemsToBuildingBlocksTabGroup);
    }
}
