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
import net.myriantics.klaxon.item.consumables.EnderPlateItem;
import net.myriantics.klaxon.item.tools.HammerItem;

public class KlaxonItems {
    public static final Item HAMMER = registerItem("hammer", new HammerItem(new FabricItemSettings().maxCount(1).maxDamage(512)));
    public static final Item ENDER_PEARL_PLATE_ITEM = registerItem("ender_plate", new EnderPlateItem(new FabricItemSettings()));
    public static final Item STEEL_INGOT = registerItem("steel_ingot", new Item(new FabricItemSettings()));

    private static void addItemsToToolTabGroup(FabricItemGroupEntries entries) {
        entries.add(HAMMER);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(KlaxonCommon.MOD_ID, name), item);
    }

    public static void registerModItems() {
        KlaxonCommon.LOGGER.info("Registering Klaxon's Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(KlaxonItems::addItemsToToolTabGroup);
    }
}
