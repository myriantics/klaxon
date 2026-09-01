package net.myriantics.klaxon.datagen.loot_table;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.myriantics.klaxon.registry.dynamic.KlaxonLootTables;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class KlaxonEntityLootTableProvider extends SimpleFabricLootTableProvider {
    public KlaxonEntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(
                KlaxonEntityTypes.OMINOUS_DEEPSLATE_BLAST_PROCESSOR.value().getDefaultLootTable(),
                new LootTable.Builder().pool(
                        LootPool.lootPool()
                                .add(
                                        LootItem.lootTableItem(KlaxonItems.STEEL_HORSE_ARMOR.value())
                                ).build()
                )
        );
    }
}
