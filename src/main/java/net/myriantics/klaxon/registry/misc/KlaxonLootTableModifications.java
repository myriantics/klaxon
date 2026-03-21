package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonLootTableModifications {
    public static void modify(ResourceKey<LootTable> lootTableRegistryKey, LootTable.Builder builder, LootTableSource lootTableSource, HolderLookup.Provider wrapperLookup) {
        // don't override datapacked stuff
        if (!lootTableSource.isBuiltin()) return;

        // add hallnox pod
        if (BuiltInLootTables.SNIFFER_DIGGING.equals(lootTableRegistryKey)) {
            builder.modifyPools((builder1 -> builder1.add(LootItem.lootTableItem(KlaxonItems.HALLNOX_POD))));
        }
    }
}
