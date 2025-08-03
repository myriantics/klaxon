package net.myriantics.klaxon.datagen.loot_table;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

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
        addDrop(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE);

        // steel
        addDrop(KlaxonBlocks.STEEL_BLOCK);
        addDrop(KlaxonBlocks.STEEL_CASING);
        addDrop(KlaxonBlocks.STEEL_PLATING_BLOCK);
        addDrop(KlaxonBlocks.STEEL_DOOR, this::doorDrops);
        addDrop(KlaxonBlocks.STEEL_TRAPDOOR);

        // crude steel
        addDrop(KlaxonBlocks.CRUDE_STEEL_BLOCK);
        addDrop(KlaxonBlocks.CRUDE_STEEL_CASING);
        addDrop(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);
        addDrop(KlaxonBlocks.CRUDE_STEEL_DOOR, this::doorDrops);
        addDrop(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);

        // iron
        addDrop(KlaxonBlocks.IRON_PLATING_BLOCK);

        // gold
        addDrop(KlaxonBlocks.GOLD_PLATING_BLOCK);

        // copper
        addDrop(KlaxonBlocks.COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
        addDrop(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

        // rubber
        addDrop(KlaxonBlocks.RUBBER_BLOCK);
        addDrop(KlaxonBlocks.RUBBER_SHEET_BLOCK);

        // molten rubber
        addDrop(KlaxonBlocks.MOLTEN_RUBBER_BLOCK);

        // hallnox
        addDrop(KlaxonBlocks.HALLNOX_STEM);
        addDrop(KlaxonBlocks.HALLNOX_HYPHAE);
        addDrop(KlaxonBlocks.STRIPPED_HALLNOX_STEM);
        addDrop(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE);
        addDrop(KlaxonBlocks.HALLNOX_PLANKS);
        addDrop(KlaxonBlocks.HALLNOX_STAIRS);
        addDrop(KlaxonBlocks.HALLNOX_SLAB);
        addDrop(KlaxonBlocks.HALLNOX_FENCE);
        addDrop(KlaxonBlocks.HALLNOX_FENCE_GATE);
        addDrop(KlaxonBlocks.HALLNOX_DOOR, this::doorDrops);
        addDrop(KlaxonBlocks.HALLNOX_TRAPDOOR);
        addDrop(KlaxonBlocks.HALLNOX_PRESSURE_PLATE);
        addDrop(KlaxonBlocks.HALLNOX_BUTTON);
        addDrop(KlaxonBlocks.HALLNOX_POD);
        addPottedPlantDrops(KlaxonBlocks.POTTED_HALLNOX_POD);
        addDrop(KlaxonBlocks.HALLNOX_WART_BLOCK);
        addDrop(KlaxonBlocks.HALLNOX_SIGN);
        addDrop(KlaxonBlocks.HALLNOX_HANGING_SIGN);
        addDrop(KlaxonBlocks.HALLNOX_BULB);
    }
}