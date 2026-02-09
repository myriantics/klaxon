package net.myriantics.klaxon.datagen.loot_table;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferHelper;
import net.myriantics.klaxon.registry.dynamic.KlaxonLootTables;
import net.myriantics.klaxon.registry.item.KlaxonItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class KlaxonGameplayLootTableProvider extends SimpleFabricLootTableProvider {
    public KlaxonGameplayLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup, LootContextTypes.GIFT);
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        lootTableBiConsumer.accept(
                KlaxonLootTables.GERALD_SNIFFER_GAMEPLAY,
                new LootTable.Builder().pool(
                        LootPool.builder()
                                .with(
                                        ItemEntry.builder(KlaxonItems.CRESTED_STEEL_HELMET)
                                ).build()
                )
        );
    }
}
