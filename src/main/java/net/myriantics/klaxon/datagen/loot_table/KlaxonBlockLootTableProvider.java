package net.myriantics.klaxon.datagen.loot_table;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.registry.KlaxonBlocks;

import java.util.concurrent.CompletableFuture;

public class KlaxonBlockLootTableProvider extends FabricBlockLootTableProvider {

    public KlaxonBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        // machines
        addDrop(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, drops(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));

        // storage blocks
        addDrop(KlaxonBlocks.STEEL_BLOCK, drops(KlaxonBlocks.STEEL_BLOCK));
        addDrop(KlaxonBlocks.CRUDE_STEEL_BLOCK, drops(KlaxonBlocks.CRUDE_STEEL_BLOCK));

        // plate storage blocks
        addDrop(KlaxonBlocks.STEEL_PLATING_BLOCK, drops(KlaxonBlocks.STEEL_PLATING_BLOCK));
        addDrop(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK, drops(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK));
        addDrop(KlaxonBlocks.IRON_PLATING_BLOCK, drops(KlaxonBlocks.IRON_PLATING_BLOCK));
        addDrop(KlaxonBlocks.GOLD_PLATING_BLOCK, drops(KlaxonBlocks.GOLD_PLATING_BLOCK));
        addDrop(KlaxonBlocks.COPPER_PLATING_BLOCK, drops(KlaxonBlocks.COPPER_PLATING_BLOCK));

        // decor
        doorDrops(KlaxonBlocks.STEEL_DOOR);
        addDrop(KlaxonBlocks.STEEL_TRAPDOOR);
        doorDrops(KlaxonBlocks.CRUDE_STEEL_DOOR);
        addDrop(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);
    }
}