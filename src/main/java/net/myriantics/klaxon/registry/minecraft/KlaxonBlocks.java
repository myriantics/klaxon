package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.decor.MoltenRubberBlock;
import net.myriantics.klaxon.block.customblocks.decor.OxidizablePillarBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.decor.SteelDoorBlock;
import net.myriantics.klaxon.block.customblocks.decor.SteelTrapdoorBlock;
import net.myriantics.klaxon.block.customblocks.machines.nether_reactor_core.NetherReactorCoreBlock;

public class KlaxonBlocks {

    // steel
    public static final Block STEEL_BLOCK = registerBlock("steel_block",
            new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
    public static final Block STEEL_PLATING_BLOCK = registerBlock("steel_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(STEEL_BLOCK)));
    public static final Block STEEL_DOOR = registerBlock("steel_door",
            new SteelDoorBlock(KlaxonBlockSetTypes.STEEL, AbstractBlock.Settings.copy(STEEL_BLOCK).nonOpaque()));
    public static final Block STEEL_TRAPDOOR = registerBlock("steel_trapdoor",
            new SteelTrapdoorBlock(KlaxonBlockSetTypes.STEEL, AbstractBlock.Settings.copy(STEEL_DOOR)));

    // crude steel
    public static final Block CRUDE_STEEL_BLOCK = registerBlock("crude_steel_block",
            new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).pistonBehavior(PistonBehavior.DESTROY).strength(2.5f, 3.0f)));
    public static final Block CRUDE_STEEL_PLATING_BLOCK = registerBlock("crude_steel_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(CRUDE_STEEL_BLOCK)));
    public static final Block CRUDE_STEEL_DOOR = registerBlock("crude_steel_door",
            new SteelDoorBlock(KlaxonBlockSetTypes.CRUDE_STEEL, AbstractBlock.Settings.copy(CRUDE_STEEL_BLOCK).nonOpaque()));
    public static final Block CRUDE_STEEL_TRAPDOOR = registerBlock("crude_steel_trapdoor",
            new SteelTrapdoorBlock(KlaxonBlockSetTypes.CRUDE_STEEL, AbstractBlock.Settings.copy(CRUDE_STEEL_DOOR)));


    // machines
    public static final Block DEEPSLATE_BLAST_PROCESSOR = registerBlock("deepslate_blast_processor",
            new DeepslateBlastProcessorBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_DEEPSLATE).luminance(Blocks.createLightLevelFromLitBlockState(15))));
    public static final Block STEEL_CASING = registerBlock("steel_casing",
            new Block(AbstractBlock.Settings.copy(KlaxonBlocks.STEEL_BLOCK)));
    public static final Block CRUDE_STEEL_CASING = registerBlock("crude_steel_casing",
            new Block(AbstractBlock.Settings.copy(KlaxonBlocks.CRUDE_STEEL_BLOCK)));
    public static final Block NETHER_REACTOR_CORE = registerBlock("nether_reactor_core",
            new NetherReactorCoreBlock(AbstractBlock.Settings.copy(KlaxonBlocks.STEEL_CASING).luminance((state) -> 12)));
    // iron
    public static final Block IRON_PLATING_BLOCK = registerBlock("iron_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));

    // gold
    public static final Block GOLD_PLATING_BLOCK = registerBlock("gold_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.GOLD_BLOCK)));

    // copper
    public static final Block COPPER_PLATING_BLOCK = registerBlock("copper_plating_block",
            new OxidizablePillarBlock(Oxidizable.OxidationLevel.UNAFFECTED, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK)));
    public static final Block EXPOSED_COPPER_PLATING_BLOCK = registerBlock("exposed_copper_plating_block",
            new OxidizablePillarBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(Blocks.EXPOSED_COPPER)));
    public static final Block WEATHERED_COPPER_PLATING_BLOCK = registerBlock("weathered_copper_plating_block",
            new OxidizablePillarBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(Blocks.WEATHERED_COPPER)));
    public static final Block OXIDIZED_COPPER_PLATING_BLOCK = registerBlock("oxidized_copper_plating_block",
            new OxidizablePillarBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(Blocks.OXIDIZED_COPPER)));
    public static final Block WAXED_COPPER_PLATING_BLOCK = registerBlock("waxed_copper_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK)));
    public static final Block WAXED_EXPOSED_COPPER_PLATING_BLOCK = registerBlock("waxed_exposed_copper_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.EXPOSED_COPPER)));
    public static final Block WAXED_WEATHERED_COPPER_PLATING_BLOCK = registerBlock("waxed_weathered_copper_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.WEATHERED_COPPER)));
    public static final Block WAXED_OXIDIZED_COPPER_PLATING_BLOCK = registerBlock("waxed_oxidized_copper_plating_block",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OXIDIZED_COPPER)));

    // rubber
    public static final Block RUBBER_BLOCK = registerBlock("rubber_block",
            new Block(AbstractBlock.Settings.copy(Blocks.SHROOMLIGHT)
                    .sounds(BlockSoundGroup.SHROOMLIGHT)
                    .mapColor(MapColor.DARK_CRIMSON)
                    .strength(3.0f)
                    .luminance((state) -> 0)));
    public static final Block RUBBER_SHEET_BLOCK = registerBlock("rubber_sheet_block",
            new PillarBlock(AbstractBlock.Settings.copy(RUBBER_BLOCK)));

    // molten rubber
    public static final Block MOLTEN_RUBBER_BLOCK = registerBlock("molten_rubber_block",
            new MoltenRubberBlock(AbstractBlock.Settings.copy(RUBBER_BLOCK)
                    .luminance(state -> 3)
                    .emissiveLighting(Blocks::always)
                    .allowsSpawning((state, world, pos, entityType) -> entityType.isFireImmune())
                    .postProcess(Blocks::always)
                    .mapColor(MapColor.DARK_RED)
                    .strength(1.5F)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, KlaxonCommon.locate(name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blocks!");
    }

}
