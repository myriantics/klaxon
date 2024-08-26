package net.myriantics.klaxon.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.item.consumables.EnderPlateItem;
import net.myriantics.klaxon.item.tools.HammerItem;

public class KlaxonItems {
    public static final Item HAMMER = registerItem("hammer", new HammerItem(new FabricItemSettings().maxCount(1).maxDamage(512)));
    public static final Item ENDER_PEARL_PLATE_ITEM = null;// = registerItem("ender_plate", new EnderPlateItem(new FabricItemSettings()));
    public static final Item STEEL_INGOT = registerItem("steel_ingot", new Item(new FabricItemSettings()));
    public static final Item FRACTURED_RAW_IRON = registerItem("fractured_raw_iron", new Item(new FabricItemSettings()));
    public static final Item CRUDE_STEEL_MIXTURE = registerItem("crude_steel_mixture", new Item(new FabricItemSettings()));
    public static final Item STEEL_NUGGET = registerItem("steel_nugget", new Item(new FabricItemSettings()));

    private static void addItemsToToolTabGroup(FabricItemGroupEntries entries) {
        entries.add(HAMMER);
    }

    private static void addItemsToRedstoneTabGroup(FabricItemGroupEntries entries) {
        entries.add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
        entries.add(KlaxonItems.HAMMER);
    }

    private static void addItemsToCombatTabGroup(FabricItemGroupEntries entries) {
        entries.add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
        entries.add(KlaxonItems.HAMMER);
    }

    private static void addItemsToIngredientTabGroup(FabricItemGroupEntries entries) {
        entries.add(KlaxonItems.FRACTURED_RAW_IRON);
        entries.add(KlaxonItems.CRUDE_STEEL_MIXTURE);
        entries.add(KlaxonItems.STEEL_INGOT);
        entries.add(KlaxonItems.STEEL_NUGGET);
        entries.add(KlaxonBlocks.STEEL_BLOCK);
    }

    private static void addItemsToBuildingBlocksTabGroup(FabricItemGroupEntries entries) {
        entries.add(KlaxonBlocks.STEEL_BLOCK);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(KlaxonCommon.MOD_ID, name), item);
    }

    public static void registerModItems() {
        KlaxonCommon.LOGGER.info("Registering Klaxon's Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(KlaxonItems::addItemsToToolTabGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(KlaxonItems::addItemsToRedstoneTabGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(KlaxonItems::addItemsToCombatTabGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(KlaxonItems::addItemsToIngredientTabGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(KlaxonItems::addItemsToBuildingBlocksTabGroup);
    }
}
