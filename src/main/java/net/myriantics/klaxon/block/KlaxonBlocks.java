package net.myriantics.klaxon.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.BlastProcessorBlock;

public class KlaxonBlocks {

    public static final Block DEEPSLATE_BLAST_PROCESSOR = registerBlock("deepslate_blast_processor",
            new BlastProcessorBlock(FabricBlockSettings.copyOf(Blocks.POLISHED_DEEPSLATE)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(KlaxonCommon.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(KlaxonCommon.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static final void registerModBlocks() {
        KlaxonCommon.LOGGER.info("Registering Blocks for Klaxon");
    }

}
