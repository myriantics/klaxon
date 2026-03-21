package net.myriantics.klaxon.datagen.model.block;

import net.minecraft.data.models.BlockModelGenerators;
import net.myriantics.klaxon.datagen.model.KlaxonBlockModelSubProvider;
import net.myriantics.klaxon.datagen.model.KlaxonModelProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public class KlaxonSimpleBlockModelProvider extends KlaxonBlockModelSubProvider {
    public KlaxonSimpleBlockModelProvider(KlaxonModelProvider provider, BlockModelGenerators generator) {
        super(provider, generator);
    }

    @Override
    public void generateModels() {
        registerSimpleBlockStateModels();
        registerAxisRotatedBlockModels();
    }

    private void registerSimpleBlockStateModels() {
        // steel
        registerSimpleCubeAll(KlaxonBlocks.STEEL_BLOCK);
        registerSimpleCubeAll(KlaxonBlocks.STEEL_CASING);

        // crude steel
        registerSimpleCubeAll(KlaxonBlocks.CRUDE_STEEL_BLOCK);
        registerSimpleCubeAll(KlaxonBlocks.CRUDE_STEEL_CASING);

        // rubber
        registerSimpleCubeAll(KlaxonBlocks.RUBBER_BLOCK);
        registerSimpleCubeAll(KlaxonBlocks.MOLTEN_RUBBER_BLOCK);

        // hallnox
        registerSimpleCubeAll(KlaxonBlocks.HALLNOX_WART_BLOCK);
    }

    private void registerAxisRotatedBlockModels() {
        // hallnox
        registerLog(KlaxonBlocks.HALLNOX_STEM).log(KlaxonBlocks.HALLNOX_STEM).wood(KlaxonBlocks.HALLNOX_HYPHAE);
        registerLog(KlaxonBlocks.STRIPPED_HALLNOX_STEM).log(KlaxonBlocks.STRIPPED_HALLNOX_STEM).wood(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE);

        // steel
        registerPillarBlock(KlaxonBlocks.STEEL_PLATING_BLOCK);
        registerPillarBlock(KlaxonBlocks.STEEL_WIRE_SPOOL_BLOCK);

        // crude steel
        registerPillarBlock(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);

        // iron
        registerPillarBlock(KlaxonBlocks.IRON_PLATING_BLOCK);
        registerPillarBlock(KlaxonBlocks.IRON_WIRE_SPOOL_BLOCK);

        // gold
        registerPillarBlock(KlaxonBlocks.GOLD_PLATING_BLOCK);
        registerPillarBlock(KlaxonBlocks.GOLD_WIRE_SPOOL_BLOCK);

        // copper // oxidation is so fun :D
        registerPillarBlock(KlaxonBlocks.COPPER_PLATING_BLOCK);
        registerPillarBlock(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
        registerPillarBlock(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
        registerPillarBlock(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);
        registerOxidizedPillarBlock(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK, KlaxonBlocks.COPPER_PLATING_BLOCK);
        registerOxidizedPillarBlock(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK, KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
        registerOxidizedPillarBlock(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK, KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
        registerOxidizedPillarBlock(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK, KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);
        registerPillarBlock(KlaxonBlocks.COPPER_WIRE_SPOOL_BLOCK);
        registerPillarBlock(KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
        registerPillarBlock(KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
        registerPillarBlock(KlaxonBlocks.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);
        registerOxidizedPillarBlock(KlaxonBlocks.WAXED_COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.COPPER_WIRE_SPOOL_BLOCK);
        registerOxidizedPillarBlock(KlaxonBlocks.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
        registerOxidizedPillarBlock(KlaxonBlocks.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
        registerOxidizedPillarBlock(KlaxonBlocks.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);

        // rubber
        registerPillarBlock(KlaxonBlocks.RUBBER_SHEET_BLOCK);
    }
}
