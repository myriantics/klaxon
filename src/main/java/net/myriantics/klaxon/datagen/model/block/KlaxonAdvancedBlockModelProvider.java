package net.myriantics.klaxon.datagen.model.block;

import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.registry.Registries;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.decor.HallnoxBulbBlock;
import net.myriantics.klaxon.datagen.model.KlaxonBlockModelSubProvider;
import net.myriantics.klaxon.datagen.model.KlaxonModelProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlockFamilies;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public class KlaxonAdvancedBlockModelProvider extends KlaxonBlockModelSubProvider {
    public KlaxonAdvancedBlockModelProvider(KlaxonModelProvider provider, BlockStateModelGenerator generator) {
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

        // crude steel
        registerOrientableTrapdoor(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);
        registerOrientableTrapdoor(KlaxonBlocks.STEEL_TRAPDOOR);

        // hallnox
        registerHangingSign(KlaxonBlocks.STRIPPED_HALLNOX_STEM, KlaxonBlocks.HALLNOX_HANGING_SIGN, KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN);
        registerCubeAllModelTexturePool(KlaxonBlockFamilies.HALLNOX.getBaseBlock()).family(KlaxonBlockFamilies.HALLNOX);
        acceptSingletonBlockState(KlaxonBlocks.POTTED_HALLNOX_POD, ModelIds.getBlockModelId(KlaxonBlocks.POTTED_HALLNOX_POD));
        registerHallnoxPod(generator);
        registerHallnoxBulb((HallnoxBulbBlock) KlaxonBlocks.HALLNOX_BULB);
    }

    private void registerMachineBlockStateModels() {
        // blast processors
        registerDeepslateBlastProcessor();

        // nether reactors
        acceptSingletonBlockState(KlaxonBlocks.NETHER_REACTOR_CORE, KlaxonCommon.locate("block/" + Registries.BLOCK.getId(KlaxonBlocks.NETHER_REACTOR_CORE).getPath()));
        acceptSingletonBlockState(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE, KlaxonCommon.locate("block/" + Registries.BLOCK.getId(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE).getPath()));

        // pipe matrices
        registerPipeMatrixUBend(KlaxonBlocks.COPPER_PIPE_MATRIX_U_BEND, KlaxonItems.COPPER_PIPE_MATRIX);
        registerPipeMatrixSegment(KlaxonBlocks.COPPER_PIPE_MATRIX_SEGMENT, KlaxonItems.COPPER_PIPE_MATRIX);
    }
}
