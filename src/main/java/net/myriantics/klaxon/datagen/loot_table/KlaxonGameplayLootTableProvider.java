package net.myriantics.klaxon.datagen.loot_table;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.myriantics.klaxon.registry.dynamic.KlaxonLootTables;
import net.myriantics.klaxon.registry.item.KlaxonItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class KlaxonGameplayLootTableProvider extends SimpleFabricLootTableProvider {
    public KlaxonGameplayLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.GIFT);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        lootTableBiConsumer.accept(
                KlaxonLootTables.GERALD_SNIFFER_GAMEPLAY,
                new LootTable.Builder().pool(
                        LootPool.lootPool()
                                .add(
                                        LootItem.lootTableItem(KlaxonItems.CRESTED_STEEL_HELMET.value())
                                ).build()
                )
        );
    }
}
