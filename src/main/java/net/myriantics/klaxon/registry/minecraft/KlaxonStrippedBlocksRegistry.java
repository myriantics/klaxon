package net.myriantics.klaxon.registry.minecraft;

import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.mixin.content.registry.AxeItemAccessor;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonStrippedBlocksRegistry {
    static {
        StrippableBlockRegistry.register(KlaxonBlocks.HALLNOX_STEM, KlaxonBlocks.STRIPPED_HALLNOX_STEM);
        StrippableBlockRegistry.register(KlaxonBlocks.HALLNOX_HYPHAE, KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Stripped Blocks!");
    }
}
