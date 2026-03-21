package net.myriantics.klaxon.registry.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.decor.OxidizablePillarBlock;
import net.myriantics.klaxon.block.decor.SteelDoorBlock;
import net.myriantics.klaxon.block.decor.SteelTrapdoorBlock;
import net.myriantics.klaxon.block.decor.hallnox_bulb.HallnoxBulbBlock;
import net.myriantics.klaxon.block.functional.MoltenRubberBlock;
import net.myriantics.klaxon.block.functional.hallnox_pod.HallnoxPodBlock;
import net.myriantics.klaxon.block.machines.CasingBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.OxidizablePipeMatrixSegmentBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.OxidizablePipeMatrixUBendBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.PipeMatrixSegmentBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.PipeMatrixUBendBlock;
import net.myriantics.klaxon.block.machines.nether_reactor_core.NetherReactorCoreBlock;
import net.myriantics.klaxon.registry.worldgen.KlaxonSaplingGenerators;

public abstract class KlaxonBlocks {

    // steel
    public static final Block STEEL_BLOCK = registerBlock("steel_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final Block STEEL_PLATING_BLOCK = registerBlock("steel_plating_block",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK)));
    public static final Block STEEL_CASING = registerBlock("steel_casing",
            new CasingBlock(BlockBehaviour.Properties.ofFullCopy(KlaxonBlocks.STEEL_BLOCK)));
    public static final Block STEEL_WIRE_SPOOL_BLOCK = registerBlock("steel_wire_spool",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK)));
    public static final Block STEEL_DOOR = registerBlock("steel_door",
            new SteelDoorBlock(KlaxonBlockSetTypes.STEEL, BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK).noOcclusion()));
    public static final Block STEEL_TRAPDOOR = registerBlock("steel_trapdoor",
            new SteelTrapdoorBlock(KlaxonBlockSetTypes.STEEL, BlockBehaviour.Properties.ofFullCopy(STEEL_DOOR)));


    // crude steel
    public static final Block CRUDE_STEEL_BLOCK = registerBlock("crude_steel_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).pushReaction(PushReaction.DESTROY).strength(2.5f, 3.0f)));
    public static final Block CRUDE_STEEL_PLATING_BLOCK = registerBlock("crude_steel_plating_block",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(CRUDE_STEEL_BLOCK)));
    public static final Block CRUDE_STEEL_CASING = registerBlock("crude_steel_casing",
            new CasingBlock(BlockBehaviour.Properties.ofFullCopy(KlaxonBlocks.CRUDE_STEEL_BLOCK)));
    public static final Block CRUDE_STEEL_DOOR = registerBlock("crude_steel_door",
            new SteelDoorBlock(KlaxonBlockSetTypes.CRUDE_STEEL, BlockBehaviour.Properties.ofFullCopy(CRUDE_STEEL_BLOCK).noOcclusion()));
    public static final Block CRUDE_STEEL_TRAPDOOR = registerBlock("crude_steel_trapdoor",
            new SteelTrapdoorBlock(KlaxonBlockSetTypes.CRUDE_STEEL, BlockBehaviour.Properties.ofFullCopy(CRUDE_STEEL_DOOR)));


    // machines
    public static final Block DEEPSLATE_BLAST_PROCESSOR = registerBlock("deepslate_blast_processor",
            new DeepslateBlastProcessorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DEEPSLATE).lightLevel(Blocks.litBlockEmission(15))));
    public static final Block NETHER_REACTOR_CORE = registerBlock("nether_reactor_core",
            new NetherReactorCoreBlock(BlockBehaviour.Properties.ofFullCopy(KlaxonBlocks.STEEL_CASING).lightLevel((state) -> 15).noOcclusion()));
    public static final Block CRUDE_NETHER_REACTOR_CORE = registerBlock("crude_nether_reactor_core",
            new NetherReactorCoreBlock(BlockBehaviour.Properties.ofFullCopy(KlaxonBlocks.CRUDE_STEEL_CASING).lightLevel((state) -> 12).noOcclusion()));

    // pipe matrices
    public static final Block COPPER_PIPE_MATRIX_U_BEND = registerBlock("copper_pipe_matrix_u_bend",
            new OxidizablePipeMatrixUBendBlock(
                    WeatheringCopper.WeatherState.UNAFFECTED,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
            )
    );
    public static final Block COPPER_PIPE_MATRIX_SEGMENT = registerBlock("copper_pipe_matrix_segment",
            new OxidizablePipeMatrixSegmentBlock(
                    WeatheringCopper.WeatherState.UNAFFECTED,
                    BlockBehaviour.Properties.ofFullCopy(COPPER_PIPE_MATRIX_U_BEND),
                    COPPER_PIPE_MATRIX_U_BEND
            )
    );
    public static final Block EXPOSED_COPPER_PIPE_MATRIX_U_BEND = registerBlock("exposed_copper_pipe_matrix_u_bend",
            new OxidizablePipeMatrixUBendBlock(
                    WeatheringCopper.WeatherState.EXPOSED,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)
            )
    );
    public static final Block EXPOSED_COPPER_PIPE_MATRIX_SEGMENT = registerBlock("exposed_copper_pipe_matrix_segment",
            new OxidizablePipeMatrixSegmentBlock(
                    WeatheringCopper.WeatherState.EXPOSED,
                    BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER_PIPE_MATRIX_U_BEND),
                    EXPOSED_COPPER_PIPE_MATRIX_U_BEND
            )
    );
    public static final Block WEATHERED_COPPER_PIPE_MATRIX_U_BEND = registerBlock("weathered_copper_pipe_matrix_u_bend",
            new OxidizablePipeMatrixUBendBlock(
                    WeatheringCopper.WeatherState.WEATHERED,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)
            )
    );
    public static final Block WEATHERED_COPPER_PIPE_MATRIX_SEGMENT = registerBlock("weathered_copper_pipe_matrix_segment",
            new OxidizablePipeMatrixSegmentBlock(
                    WeatheringCopper.WeatherState.WEATHERED,
                    BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER_PIPE_MATRIX_U_BEND),
                    WEATHERED_COPPER_PIPE_MATRIX_U_BEND
            )
    );
    public static final Block OXIDIZED_COPPER_PIPE_MATRIX_U_BEND = registerBlock("oxidized_copper_pipe_matrix_u_bend",
            new OxidizablePipeMatrixUBendBlock(
                    WeatheringCopper.WeatherState.OXIDIZED,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)
            )
    );
    public static final Block OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT = registerBlock("oxidized_copper_pipe_matrix_segment",
            new OxidizablePipeMatrixSegmentBlock(
                    WeatheringCopper.WeatherState.OXIDIZED,
                    BlockBehaviour.Properties.ofFullCopy(OXIDIZED_COPPER_PIPE_MATRIX_U_BEND),
                    OXIDIZED_COPPER_PIPE_MATRIX_U_BEND
            )
    );
    public static final Block WAXED_COPPER_PIPE_MATRIX_U_BEND = registerBlock("waxed_copper_pipe_matrix_u_bend",
            new PipeMatrixUBendBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_BLOCK)
            )
    );
    public static final Block WAXED_COPPER_PIPE_MATRIX_SEGMENT = registerBlock("waxed_copper_pipe_matrix_segment",
            new PipeMatrixSegmentBlock(
                    BlockBehaviour.Properties.ofFullCopy(WAXED_COPPER_PIPE_MATRIX_U_BEND),
                    WAXED_COPPER_PIPE_MATRIX_U_BEND
            )
    );
    public static final Block WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND = registerBlock("waxed_exposed_copper_pipe_matrix_u_bend",
            new PipeMatrixUBendBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)
            )
    );
    public static final Block WAXED_EXPOSED_COPPER_PIPE_MATRIX_SEGMENT = registerBlock("waxed_exposed_copper_pipe_matrix_segment",
            new PipeMatrixSegmentBlock(
                    BlockBehaviour.Properties.ofFullCopy(WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND),
                    WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND
            )
    );
    public static final Block WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND = registerBlock("waxed_weathered_copper_pipe_matrix_u_bend",
            new PipeMatrixUBendBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)
            )
    );
    public static final Block WAXED_WEATHERED_COPPER_PIPE_MATRIX_SEGMENT = registerBlock("waxed_weathered_copper_pipe_matrix_segment",
            new PipeMatrixSegmentBlock(
                    BlockBehaviour.Properties.ofFullCopy(WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND),
                    WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND
            )
    );
    public static final Block WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND = registerBlock("waxed_oxidized_copper_pipe_matrix_u_bend",
            new PipeMatrixUBendBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)
            )
    );
    public static final Block WAXED_OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT = registerBlock("waxed_oxidized_copper_pipe_matrix_segment",
            new PipeMatrixSegmentBlock(
                    BlockBehaviour.Properties.ofFullCopy(WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND),
                    WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND
            )
    );


    // hallnox
    public static final Block HALLNOX_POD = registerBlock("hallnox_pod",
            new HallnoxPodBlock(
                    KlaxonSaplingGenerators.HALLNOX,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SHROOMLIGHT).lightLevel((state) -> 12)
            )
    );
    public static final Block POTTED_HALLNOX_POD = registerBlock("potted_hallnox_pod",
            new FlowerPotBlock(
                    HALLNOX_POD,
                    BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).lightLevel((state) -> 12)
            )
    );
    public static final Block HALLNOX_WART_BLOCK = registerBlock("hallnox_wart_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_WART_BLOCK).mapColor(MapColor.TERRACOTTA_GRAY).strength(1.0f, 2.0f))
    );
    public static final Block HALLNOX_STEM = registerBlock("hallnox_stem",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_STEM).mapColor(MapColor.TERRACOTTA_GRAY).strength(3.0f, 4.0f))
    );
    public static final Block STRIPPED_HALLNOX_STEM = registerBlock("stripped_hallnox_stem",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(HALLNOX_STEM))
    );
    public static final Block HALLNOX_HYPHAE = registerBlock("hallnox_hyphae",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(HALLNOX_STEM))
    );
    public static final Block STRIPPED_HALLNOX_HYPHAE = registerBlock("stripped_hallnox_hyphae",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(HALLNOX_STEM))
    );
    public static final Block HALLNOX_PLANKS = registerBlock("hallnox_planks",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_PLANKS).mapColor(MapColor.TERRACOTTA_GRAY))
    );
    public static final Block HALLNOX_STAIRS = registerBlock("hallnox_stairs",
            new StairBlock(HALLNOX_PLANKS.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(HALLNOX_PLANKS))
    );
    public static final Block HALLNOX_SLAB = registerBlock("hallnox_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(HALLNOX_PLANKS))
    );
    public static final Block HALLNOX_PRESSURE_PLATE = registerBlock("hallnox_pressure_plate",
            new PressurePlateBlock(KlaxonBlockSetTypes.HALLNOX, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_PRESSURE_PLATE)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(1.0f))
    );
    public static final Block HALLNOX_BUTTON = registerBlock("hallnox_button",
            new ButtonBlock(KlaxonBlockSetTypes.HALLNOX, 40, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_BUTTON)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(1.0f))
    );
    public static final Block HALLNOX_TRAPDOOR = registerBlock("hallnox_trapdoor",
            new TrapDoorBlock(KlaxonBlockSetTypes.HALLNOX, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_TRAPDOOR)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Block HALLNOX_DOOR = registerBlock("hallnox_door",
            new DoorBlock(KlaxonBlockSetTypes.HALLNOX, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_DOOR)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Block HALLNOX_SIGN = registerBlock("hallnox_sign",
            new StandingSignBlock(KlaxonWoodTypes.HALLNOX, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
            )
    );
    public static final Block HALLNOX_WALL_SIGN = registerBlock("hallnox_wall_sign",
            new WallSignBlock(KlaxonWoodTypes.HALLNOX, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_WALL_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
                    .dropsLike(HALLNOX_SIGN)
            )
    );
    public static final Block HALLNOX_HANGING_SIGN = registerBlock("hallnox_hanging_sign",
            new CeilingHangingSignBlock(KlaxonWoodTypes.HALLNOX, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_HANGING_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
            )
    );
    public static final Block HALLNOX_WALL_HANGING_SIGN = registerBlock("hallnox_wall_hanging_sign",
            new WallHangingSignBlock(KlaxonWoodTypes.HALLNOX, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_WALL_HANGING_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
                    .dropsLike(HALLNOX_HANGING_SIGN))
    );
    public static final Block HALLNOX_FENCE = registerBlock("hallnox_fence",
            new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_FENCE)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Block HALLNOX_FENCE_GATE = registerBlock("hallnox_fence_gate",
            new FenceGateBlock(KlaxonWoodTypes.HALLNOX, BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_FENCE)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Block HALLNOX_BULB = registerBlock("hallnox_bulb",
            new HallnoxBulbBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).lightLevel((state) -> 15))
    );


    // iron
    public static final Block IRON_PLATING_BLOCK = registerBlock("iron_plating_block",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final Block IRON_WIRE_SPOOL_BLOCK = registerBlock("iron_wire_spool",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    // gold
    public static final Block GOLD_PLATING_BLOCK = registerBlock("gold_plating_block",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK)));
    public static final Block GOLD_WIRE_SPOOL_BLOCK = registerBlock("gold_wire_spool",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK)));

    // copper plating blocks
    public static final Block COPPER_PLATING_BLOCK = registerBlock("copper_plating_block",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final Block EXPOSED_COPPER_PLATING_BLOCK = registerBlock("exposed_copper_plating_block",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));
    public static final Block WEATHERED_COPPER_PLATING_BLOCK = registerBlock("weathered_copper_plating_block",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));
    public static final Block OXIDIZED_COPPER_PLATING_BLOCK = registerBlock("oxidized_copper_plating_block",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));
    public static final Block WAXED_COPPER_PLATING_BLOCK = registerBlock("waxed_copper_plating_block",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final Block WAXED_EXPOSED_COPPER_PLATING_BLOCK = registerBlock("waxed_exposed_copper_plating_block",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));
    public static final Block WAXED_WEATHERED_COPPER_PLATING_BLOCK = registerBlock("waxed_weathered_copper_plating_block",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));
    public static final Block WAXED_OXIDIZED_COPPER_PLATING_BLOCK = registerBlock("waxed_oxidized_copper_plating_block",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));
    // copper wire spool
    public static final Block COPPER_WIRE_SPOOL_BLOCK = registerBlock("copper_wire_spool",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final Block EXPOSED_COPPER_WIRE_SPOOL_BLOCK = registerBlock("exposed_copper_wire_spool",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));
    public static final Block WEATHERED_COPPER_WIRE_SPOOL_BLOCK = registerBlock("weathered_copper_wire_spool",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));
    public static final Block OXIDIZED_COPPER_WIRE_SPOOL_BLOCK = registerBlock("oxidized_copper_wire_spool",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));
    public static final Block WAXED_COPPER_WIRE_SPOOL_BLOCK = registerBlock("waxed_copper_wire_spool",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_BLOCK)));
    public static final Block WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK = registerBlock("waxed_exposed_copper_wire_spool",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)));
    public static final Block WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK = registerBlock("waxed_weathered_copper_wire_spool",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)));
    public static final Block WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK = registerBlock("waxed_oxidized_copper_wire_spool",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)));

    // rubber
    public static final Block RUBBER_BLOCK = registerBlock("rubber_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SHROOMLIGHT)
                    .sound(SoundType.SHROOMLIGHT)
                    .mapColor(MapColor.CRIMSON_HYPHAE)
                    .strength(3.0f)
                    .lightLevel((state) -> 0)));
    public static final Block RUBBER_SHEET_BLOCK = registerBlock("rubber_sheet_block",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(RUBBER_BLOCK)));

    // molten rubber
    public static final Block MOLTEN_RUBBER_BLOCK = registerBlock("molten_rubber_block",
            new MoltenRubberBlock(BlockBehaviour.Properties.ofFullCopy(RUBBER_BLOCK)
                    .lightLevel(state -> 3)
                    .emissiveRendering(Blocks::always)
                    .isValidSpawn((state, world, pos, entityType) -> entityType.fireImmune())
                    .hasPostProcess(Blocks::always)
                    .mapColor(MapColor.NETHER)
                    .strength(1.5F)));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, KlaxonCommon.locate(name), block);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blocks!");
    }
}
