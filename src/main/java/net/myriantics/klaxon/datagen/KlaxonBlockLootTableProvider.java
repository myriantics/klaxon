package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.myriantics.klaxon.block.KlaxonBlocks;

public class KlaxonBlockLootTableProvider extends FabricBlockLootTableProvider {

    public KlaxonBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, drops(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
        addDrop(KlaxonBlocks.STEEL_BLOCK, drops(KlaxonBlocks.STEEL_BLOCK));
    }
}