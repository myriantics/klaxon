package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonLootTableModifications {
    public static void modify(RegistryKey<LootTable> lootTableRegistryKey, LootTable.Builder builder, LootTableSource lootTableSource, RegistryWrapper.WrapperLookup wrapperLookup) {
        // don't override datapacked stuff
        if (!lootTableSource.isBuiltin()) return;

        // add hallnox pod
        if (LootTables.SNIFFER_DIGGING_GAMEPLAY.equals(lootTableRegistryKey)) {
            builder.modifyPools((builder1 -> builder1.with(ItemEntry.builder(KlaxonItems.HALLNOX_POD))));
        }
    }
}
