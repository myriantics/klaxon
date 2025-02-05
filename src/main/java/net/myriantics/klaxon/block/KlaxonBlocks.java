package net.myriantics.klaxon.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.block.customblocks.DeepslateBlastProcessorBlock;

public class KlaxonBlocks {

    // cool stuff
    public static final Block DEEPSLATE_BLAST_PROCESSOR = registerBlock("deepslate_blast_processor",
            new DeepslateBlastProcessorBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_DEEPSLATE).luminance(Blocks.createLightLevelFromLitBlockState(15))));

    // filler
    public static final Block STEEL_BLOCK = registerBlock("steel_block",
            new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
    public static final Block CRUDE_STEEL_BLOCK = registerBlock("crude_steel_block",
            new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).pistonBehavior(PistonBehavior.DESTROY).strength(2.5f, 3.0f)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, KlaxonCommon.locate(name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's Blocks");
    }

}
