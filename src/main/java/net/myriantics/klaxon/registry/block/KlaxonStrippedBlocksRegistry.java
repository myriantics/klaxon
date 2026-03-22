package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonStrippedBlocksRegistry {
    static {
        register(KlaxonBlocks.HALLNOX_STEM, KlaxonBlocks.STRIPPED_HALLNOX_STEM);
        register(KlaxonBlocks.HALLNOX_HYPHAE, KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE);
    }

    private static void register(Holder<Block> natural, Holder<Block> stripped) {
        register(natural.value(), stripped.value());
    }

    private static void register(Block natural, Block stripped) {
        StrippableBlockRegistry.register(natural, stripped);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Stripped Blocks!");
    }
}
