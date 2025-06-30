package net.myriantics.klaxon.datagen.loot_table;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;

import java.util.concurrent.CompletableFuture;

public class KlaxonBlockLootTableProvider extends FabricBlockLootTableProvider {

    public KlaxonBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // machines
        addDrop(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
        addDrop(KlaxonBlocks.NETHER_REACTOR_CORE);

        // storage blocks
        addDrop(KlaxonBlocks.STEEL_BLOCK);
        addDrop(KlaxonBlocks.CRUDE_STEEL_BLOCK);
        addDrop(KlaxonBlocks.RUBBER_BLOCK);
        addDrop(KlaxonBlocks.MOLTEN_RUBBER_BLOCK);

        // casings & misc
        addDrop(KlaxonBlocks.STEEL_CASING);
        addDrop(KlaxonBlocks.CRUDE_STEEL_CASING);

        // plate storage blocks
        addDrop(KlaxonBlocks.STEEL_PLATING_BLOCK);
        addDrop(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);
        addDrop(KlaxonBlocks.IRON_PLATING_BLOCK);
        addDrop(KlaxonBlocks.GOLD_PLATING_BLOCK);

        addDrop(KlaxonBlocks.COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

        addDrop(KlaxonBlocks.RUBBER_SHEET_BLOCK);

        // decor
        addDrop(KlaxonBlocks.STEEL_DOOR, this::doorDrops);
        addDrop(KlaxonBlocks.STEEL_TRAPDOOR);
        addDrop(KlaxonBlocks.CRUDE_STEEL_DOOR, this::doorDrops);
        addDrop(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);
    }
}