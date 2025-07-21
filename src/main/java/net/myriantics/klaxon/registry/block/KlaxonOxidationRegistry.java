package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonOxidationRegistry {

    public static void init() {
        OxidizableBlocksRegistry.registerOxidizableBlockPair(KlaxonBlocks.COPPER_PLATING_BLOCK, KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK, KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK, KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);

        OxidizableBlocksRegistry.registerWaxableBlockPair(KlaxonBlocks.COPPER_PLATING_BLOCK, KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK, KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK, KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK, KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Oxidation Stages!");
    }
}
