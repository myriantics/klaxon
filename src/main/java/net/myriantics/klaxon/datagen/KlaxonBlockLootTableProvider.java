package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.block.KlaxonBlocks;

import java.util.concurrent.CompletableFuture;

public class KlaxonBlockLootTableProvider extends FabricBlockLootTableProvider {

    public KlaxonBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, drops(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
        addDrop(KlaxonBlocks.STEEL_BLOCK, drops(KlaxonBlocks.STEEL_BLOCK));
    }
}