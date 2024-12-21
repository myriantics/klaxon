package net.myriantics.klaxon.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.item.consumables.EnderPlateItem;
import net.myriantics.klaxon.item.tools.HammerItem;

import java.util.ArrayList;

public class KlaxonItems {

    public static ArrayList<Item> simpleItems = new ArrayList<>();

    // cool stuff
    public static final Item STEEL_HAMMER = registerSimpleItem("steel_hammer", new HammerItem(new Item.Settings().maxCount(1).maxDamage(512).attributeModifiers(HammerItem.createAttributeModifiers())));
    // disabled currently because its broken as FUCK - will come later trust
    public static final Item ENDER_PEARL_PLATE_ITEM = registerSimpleItem("ender_plate", new EnderPlateItem(new Item.Settings()));

    // filler
    public static final Item STEEL_INGOT = registerSimpleItem("steel_ingot", new Item(new Item.Settings()));
    public static final Item FRACTURED_RAW_IRON = registerSimpleItem("fractured_raw_iron", new Item(new Item.Settings()));
    public static final Item FRACTURED_RAW_COPPER = registerSimpleItem("fractured_raw_copper", new Item(new Item.Settings()));
    public static final Item FRACTURED_RAW_GOLD = registerSimpleItem("fractured_raw_gold", new Item(new Item.Settings()));
    public static final Item CRUDE_STEEL_MIXTURE = registerSimpleItem("crude_steel_mixture", new Item(new Item.Settings()));
    public static final Item STEEL_NUGGET = registerSimpleItem("steel_nugget", new Item(new Item.Settings()));

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
    }

    private static void addItemsToIngredientTabGroup(FabricItemGroupEntries entries) {
        entries.add(KlaxonItems.FRACTURED_RAW_IRON);
        entries.add(KlaxonItems.FRACTURED_RAW_COPPER);
        entries.add(KlaxonItems.FRACTURED_RAW_GOLD);
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
