package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonOxidationRegistry {

    public static void init() {
        // copper plating blocks
        registerOxidizable(KlaxonBlocks.COPPER_PLATING_BLOCK, KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
        registerOxidizable(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK, KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
        registerOxidizable(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK, KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);
        registerWaxable(KlaxonBlocks.COPPER_PLATING_BLOCK, KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK);
        registerWaxable(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK, KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
        registerWaxable(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK, KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
        registerWaxable(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK, KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

        // copper wire spool blocks
        registerOxidizable(KlaxonBlocks.COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
        registerOxidizable(KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
        registerOxidizable(KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);
        registerWaxable(KlaxonBlocks.COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.WAXED_COPPER_WIRE_SPOOL_BLOCK);
        registerWaxable(KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
        registerWaxable(KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
        registerWaxable(KlaxonBlocks.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK, KlaxonBlocks.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);

        // copper pipe matrix blocks
        registerOxidizable(KlaxonBlocks.COPPER_PIPE_MATRIX_U_BEND, KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_U_BEND);
        registerOxidizable(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_U_BEND, KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_U_BEND);
        registerOxidizable(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_U_BEND, KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_U_BEND);
        registerOxidizable(KlaxonBlocks.COPPER_PIPE_MATRIX_SEGMENT, KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_SEGMENT);
        registerOxidizable(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_SEGMENT);
        registerOxidizable(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT);
        registerWaxable(KlaxonBlocks.COPPER_PIPE_MATRIX_U_BEND, KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_U_BEND);
        registerWaxable(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_U_BEND, KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND);
        registerWaxable(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_U_BEND, KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND);
        registerWaxable(KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_U_BEND, KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND);
        registerWaxable(KlaxonBlocks.COPPER_PIPE_MATRIX_SEGMENT, KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_SEGMENT);
        registerWaxable(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_SEGMENT);
        registerWaxable(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_SEGMENT);
        registerWaxable(KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT, KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Oxidation Stages!");
    }

    private static void registerOxidizable(Block less, Block more) {
        OxidizableBlocksRegistry.registerOxidizableBlockPair(less, more);
    }

    private static void registerWaxable(Block unwaxed, Block waxed) {
        OxidizableBlocksRegistry.registerWaxableBlockPair(unwaxed, waxed);
    }
}
