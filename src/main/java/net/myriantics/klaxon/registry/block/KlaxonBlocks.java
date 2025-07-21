package net.myriantics.klaxon.registry.block;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.decor.*;
import net.myriantics.klaxon.block.customblocks.decor.custom_hanging_sign.CustomHangingSignBlock;
import net.myriantics.klaxon.block.customblocks.decor.custom_hanging_sign.CustomWallHangingSignBlock;
import net.myriantics.klaxon.block.customblocks.decor.custom_sign.CustomSignBlock;
import net.myriantics.klaxon.block.customblocks.decor.custom_sign.CustomWallSignBlock;
import net.myriantics.klaxon.block.customblocks.functional.HallnoxPodBlock;
import net.myriantics.klaxon.block.customblocks.functional.MoltenRubberBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.machines.nether_reactor_core.NetherReactorCoreBlock;
import net.myriantics.klaxon.registry.worldgen.KlaxonSaplingGenerators;

public abstract class KlaxonBlocks {

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
            new NetherReactorCoreBlock(AbstractBlock.Settings.copy(KlaxonBlocks.STEEL_CASING).luminance((state) -> 12).nonOpaque()));
    public static final Block CRUDE_NETHER_REACTOR_CORE = registerBlock("crude_nether_reactor_core",
            new NetherReactorCoreBlock(AbstractBlock.Settings.copy(KlaxonBlocks.CRUDE_STEEL_CASING).luminance((state) -> 10).nonOpaque()));

    // hallnox
    public static final Block HALLNOX_POD = registerBlock("hallnox_pod",
            new HallnoxPodBlock(KlaxonSaplingGenerators.HALLNOX, AbstractBlock.Settings.copy(Blocks.SHROOMLIGHT)
                    .pistonBehavior(PistonBehavior.DESTROY))
    );
    public static final Block POTTED_HALLNOX_POD = registerBlockWithoutItem("potted_hallnox_pod",
            Blocks.createFlowerPotBlock(HALLNOX_POD)
    );
    public static final Block HALLNOX_WART_BLOCK = registerBlock("hallnox_wart_block",
            new Block(AbstractBlock.Settings.copy(Blocks.WARPED_WART_BLOCK).mapColor(MapColor.TERRACOTTA_GRAY).strength(1.0f, 2.0f))
    );
    public static final Block HALLNOX_STEM = registerBlock("hallnox_stem",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.WARPED_STEM).mapColor(MapColor.TERRACOTTA_GRAY).strength(3.0f, 4.0f))
    );
    public static final Block STRIPPED_HALLNOX_STEM = registerBlock("stripped_hallnox_stem",
            new PillarBlock(AbstractBlock.Settings.copy(HALLNOX_STEM))
    );
    public static final Block HALLNOX_HYPHAE = registerBlock("hallnox_hyphae",
            new PillarBlock(AbstractBlock.Settings.copy(HALLNOX_STEM))
    );
    public static final Block STRIPPED_HALLNOX_HYPHAE = registerBlock("stripped_hallnox_hyphae",
            new PillarBlock(AbstractBlock.Settings.copy(HALLNOX_STEM))
    );
    public static final Block HALLNOX_PLANKS = registerBlock("hallnox_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.WARPED_PLANKS).mapColor(MapColor.TERRACOTTA_GRAY))
    );
    public static final Block HALLNOX_STAIRS = registerBlock("hallnox_stairs",
            new StairsBlock(HALLNOX_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(HALLNOX_PLANKS))
    );
    public static final Block HALLNOX_SLAB = registerBlock("hallnox_slab",
            new SlabBlock(AbstractBlock.Settings.copy(HALLNOX_PLANKS))
    );
    public static final Block HALLNOX_PRESSURE_PLATE = registerBlock("hallnox_pressure_plate",
            new PressurePlateBlock(KlaxonBlockSetTypes.HALLNOX, AbstractBlock.Settings.copy(Blocks.WARPED_PRESSURE_PLATE)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(1.0f))
    );
    public static final Block HALLNOX_BUTTON = registerBlock("hallnox_button",
            new ButtonBlock(KlaxonBlockSetTypes.HALLNOX, 40, AbstractBlock.Settings.copy(Blocks.WARPED_BUTTON)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(1.0f))
    );
    public static final Block HALLNOX_TRAPDOOR = registerBlock("hallnox_trapdoor",
            new TrapdoorBlock(KlaxonBlockSetTypes.HALLNOX, AbstractBlock.Settings.copy(Blocks.WARPED_TRAPDOOR)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Block HALLNOX_DOOR = registerBlock("hallnox_door",
            new DoorBlock(KlaxonBlockSetTypes.HALLNOX, AbstractBlock.Settings.copy(Blocks.WARPED_DOOR)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Block HALLNOX_SIGN = registerBlockWithoutItem("hallnox_sign",
            new CustomSignBlock(KlaxonWoodTypes.HALLNOX, AbstractBlock.Settings.copy(Blocks.WARPED_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
            )
    );
    public static final Block HALLNOX_WALL_SIGN = registerBlockWithoutItem("hallnox_wall_sign",
            new CustomWallSignBlock(KlaxonWoodTypes.HALLNOX, AbstractBlock.Settings.copy(Blocks.WARPED_WALL_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
                    .dropsLike(HALLNOX_SIGN)
            )
    );
    public static final Block HALLNOX_HANGING_SIGN = registerBlockWithoutItem("hallnox_hanging_sign",
            new CustomHangingSignBlock(KlaxonWoodTypes.HALLNOX, AbstractBlock.Settings.copy(Blocks.WARPED_HANGING_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
            )
    );
    public static final Block HALLNOX_WALL_HANGING_SIGN = registerBlockWithoutItem("hallnox_wall_hanging_sign",
            new CustomWallHangingSignBlock(KlaxonWoodTypes.HALLNOX, AbstractBlock.Settings.copy(Blocks.WARPED_WALL_HANGING_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
                    .dropsLike(HALLNOX_HANGING_SIGN))
    );
    public static final Block HALLNOX_FENCE = registerBlock("hallnox_fence",
            new FenceBlock(AbstractBlock.Settings.copy(Blocks.WARPED_FENCE)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Block HALLNOX_FENCE_GATE = registerBlock("hallnox_fence_gate",
            new FenceGateBlock(KlaxonWoodTypes.HALLNOX, AbstractBlock.Settings.copy(Blocks.WARPED_FENCE)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );


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

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, KlaxonCommon.locate(name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return registerBlockItem(name, new BlockItem(block, new Item.Settings()));
    }

    private static Item registerBlockItem(String name, BlockItem blockItem) {
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name), blockItem);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blocks!");
    }

}
