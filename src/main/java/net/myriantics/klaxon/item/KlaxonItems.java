package net.myriantics.klaxon.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonMain;
import net.myriantics.klaxon.item.customitems.HammerItem;

public class KlaxonItems {
    public static final Item HAMMER = registerItem("hammer", new HammerItem(new FabricItemSettings()));

    private static void addItemsToToolTabGroup(FabricItemGroupEntries entries) {
        entries.add(HAMMER);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(KlaxonMain.MOD_ID, name), item);
    }

    public static void registerModItems() {
        KlaxonMain.LOGGER.info("Registering Klaxon's Items");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(KlaxonItems::addItemsToToolTabGroup);
    }
}
