package net.myriantics.klaxon.registry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.SteelDoorBlock;
import net.myriantics.klaxon.block.customblocks.SteelTrapdoorBlock;

public class KlaxonBlocks {

    // cool stuff
    public static final Block DEEPSLATE_BLAST_PROCESSOR = registerBlock("deepslate_blast_processor",
            new DeepslateBlastProcessorBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_DEEPSLATE).luminance(Blocks.createLightLevelFromLitBlockState(15))));

    // storage blocks
    public static final Block STEEL_BLOCK = registerBlock("steel_block",
            new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
    public static final Block CRUDE_STEEL_BLOCK = registerBlock("crude_steel_block",
            new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).pistonBehavior(PistonBehavior.DESTROY).strength(2.5f, 3.0f)));

    // plating blocks
    public static final Block STEEL_PLATING_BLOCK = registerBlock("steel_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(STEEL_BLOCK)));
    public static final Block CRUDE_STEEL_PLATING_BLOCK = registerBlock("crude_steel_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(CRUDE_STEEL_BLOCK)));
    public static final Block IRON_PLATING_BLOCK = registerBlock("iron_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
    public static final Block GOLD_PLATING_BLOCK = registerBlock("gold_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.GOLD_BLOCK)));
    public static final Block COPPER_PLATING_BLOCK = registerBlock("copper_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK)));

    // misc decor
    public static final Block STEEL_DOOR = registerBlock("steel_door",
            new SteelDoorBlock(KlaxonBlockSetTypes.STEEL, AbstractBlock.Settings.copy(STEEL_BLOCK).nonOpaque()));
    public static final Block CRUDE_STEEL_DOOR = registerBlock("crude_steel_door",
            new SteelDoorBlock(KlaxonBlockSetTypes.CRUDE_STEEL, AbstractBlock.Settings.copy(CRUDE_STEEL_BLOCK).nonOpaque()));
    public static final Block STEEL_TRAPDOOR = registerBlock("steel_trapdoor",
            new SteelTrapdoorBlock(KlaxonBlockSetTypes.STEEL, AbstractBlock.Settings.copy(STEEL_DOOR)));
    public static final Block CRUDE_STEEL_TRAPDOOR = registerBlock("crude_steel_trapdoor",
            new SteelTrapdoorBlock(KlaxonBlockSetTypes.CRUDE_STEEL, AbstractBlock.Settings.copy(CRUDE_STEEL_DOOR)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, KlaxonCommon.locate(name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blocks!");
    }

}
