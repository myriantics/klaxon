package net.myriantics.klaxon.datagen.model.block;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.myriantics.klaxon.block.decor.hallnox_bulb.HallnoxBulbBlock;
import net.myriantics.klaxon.datagen.model.KlaxonBlockModelSubProvider;
import net.myriantics.klaxon.datagen.model.KlaxonModelProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlockFamilies;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public class KlaxonAdvancedBlockModelProvider extends KlaxonBlockModelSubProvider {
    public KlaxonAdvancedBlockModelProvider(KlaxonModelProvider provider, BlockModelGenerators generator) {
        super(provider, generator);
    }

    @Override
    public void generateModels() {
        registerMiscBlockStateModels();
        registerMachineBlockStateModels();
    }

    private void registerMiscBlockStateModels() {
        // steel
        registerDoor(KlaxonBlocks.CRUDE_STEEL_DOOR);
        registerDoor(KlaxonBlocks.STEEL_DOOR);
        registerPressurePlate(KlaxonBlocks.HEAVY_GATED_PRESSURE_PLATE.value(), KlaxonBlocks.STEEL_BLOCK.value());

        // crude steel
        registerOrientableTrapdoor(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);
        registerOrientableTrapdoor(KlaxonBlocks.STEEL_TRAPDOOR);
        registerFaultyHeavyGatedPressurePlate(KlaxonBlocks.FAULTY_HEAVY_GATED_PRESSURE_PLATE.value(), KlaxonBlocks.CRUDE_STEEL_BLOCK.value());

        // hallnox
        registerHangingSign(KlaxonBlocks.STRIPPED_HALLNOX_STEM, KlaxonBlocks.HALLNOX_HANGING_SIGN, KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN);
        registerCubeAllModelTexturePool(KlaxonBlockFamilies.HALLNOX.getBaseBlock()).generateFor(KlaxonBlockFamilies.HALLNOX);
        acceptSingletonBlockState(KlaxonBlocks.POTTED_HALLNOX_POD, ModelLocationUtils.getModelLocation(KlaxonBlocks.POTTED_HALLNOX_POD.value()));
        registerHallnoxPod(generator);
        registerHallnoxBulb((HallnoxBulbBlock) KlaxonBlocks.HALLNOX_BULB.value());
    }

    private void registerMachineBlockStateModels() {
        // blast processors
        registerDeepslateBlastProcessor(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
        registerPrecisionDispenser(KlaxonBlocks.PRECISION_DISPENSER.value());
        registerModularExplosive(KlaxonBlocks.MODULAR_EXPLOSIVE_BLOCK.value());

        // nether reactors
        registerNetherReactorCore(KlaxonBlocks.NETHER_REACTOR_CORE);
        registerNetherReactorCore(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE);

        // workstations
        registerSteelWorkbench(KlaxonBlocks.STEEL_WORKBENCH);

        // pipe matrices
        registerPipeMatrixUBend(KlaxonBlocks.COPPER_PIPE_MATRIX_U_BEND, KlaxonItems.COPPER_PIPE_MATRIX);
        registerPipeMatrixUBend(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_U_BEND, KlaxonItems.EXPOSED_COPPER_PIPE_MATRIX);
        registerPipeMatrixUBend(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_U_BEND, KlaxonItems.WEATHERED_COPPER_PIPE_MATRIX);
        registerPipeMatrixUBend(KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_U_BEND, KlaxonItems.OXIDIZED_COPPER_PIPE_MATRIX);
        registerPipeMatrixUBend(KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_U_BEND, KlaxonItems.COPPER_PIPE_MATRIX);
        registerPipeMatrixUBend(KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND, KlaxonItems.EXPOSED_COPPER_PIPE_MATRIX);
        registerPipeMatrixUBend(KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND, KlaxonItems.WAXED_WEATHERED_COPPER_PIPE_MATRIX);
        registerPipeMatrixUBend(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND, KlaxonItems.OXIDIZED_COPPER_PIPE_MATRIX);
        registerPipeMatrixSegment(KlaxonBlocks.COPPER_PIPE_MATRIX_SEGMENT, KlaxonItems.COPPER_PIPE_MATRIX.value());
        registerPipeMatrixSegment(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonItems.EXPOSED_COPPER_PIPE_MATRIX.value());
        registerPipeMatrixSegment(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonItems.WEATHERED_COPPER_PIPE_MATRIX.value());
        registerPipeMatrixSegment(KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonItems.OXIDIZED_COPPER_PIPE_MATRIX.value());
        registerPipeMatrixSegment(KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonItems.WAXED_COPPER_PIPE_MATRIX.value(), KlaxonItems.COPPER_PIPE_MATRIX.value());
        registerPipeMatrixSegment(KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonItems.WAXED_EXPOSED_COPPER_PIPE_MATRIX.value(), KlaxonItems.EXPOSED_COPPER_PIPE_MATRIX.value());
        registerPipeMatrixSegment(KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonItems.WAXED_WEATHERED_COPPER_PIPE_MATRIX.value(), KlaxonItems.WEATHERED_COPPER_PIPE_MATRIX.value());
        registerPipeMatrixSegment(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonItems.WAXED_OXIDIZED_COPPER_PIPE_MATRIX.value(), KlaxonItems.OXIDIZED_COPPER_PIPE_MATRIX.value());
    }
}
