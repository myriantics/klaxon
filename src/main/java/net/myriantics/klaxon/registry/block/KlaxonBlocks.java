package net.myriantics.klaxon.registry.block;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
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
import net.myriantics.klaxon.block.functional.SteelWorkbenchBlock;
import net.myriantics.klaxon.block.functional.hallnox_pod.HallnoxPodBlock;
import net.myriantics.klaxon.block.functional.pressure_plate.FaultyHeavyGatedPressurePlateBlock;
import net.myriantics.klaxon.block.functional.pressure_plate.HeavyGatedPressurePlateBlock;
import net.myriantics.klaxon.block.machines.CasingBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.duct.driver.aio.AIODuctDriverBlock;
import net.myriantics.klaxon.block.machines.duct.segment.DuctSegmentBlock;
import net.myriantics.klaxon.block.machines.filing_cabinet.FilingCabinetBaseBlock;
import net.myriantics.klaxon.block.machines.filing_cabinet.FilingCabinetDrawerBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.OxidizablePipeMatrixSegmentBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.OxidizablePipeMatrixUBendBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.PipeMatrixSegmentBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.PipeMatrixUBendBlock;
import net.myriantics.klaxon.block.machines.modular_explosive.ModularExplosiveBlock;
import net.myriantics.klaxon.block.machines.nether_reactor_core.NetherReactorCoreBlock;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlock;
import net.myriantics.klaxon.registry.worldgen.KlaxonSaplingGenerators;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonBlocks {

    // steel
    public static final Holder<Block> STEEL_BLOCK = register(
            "steel_block",
            new Block(
                    copyProperties(Blocks.IRON_BLOCK)
            )
    );
    public static final Holder<Block> STEEL_PLATING_BLOCK = register("steel_plating_block",
            new RotatedPillarBlock(copyProperties(STEEL_BLOCK)));
    public static final Holder<Block> STEEL_CASING = register("steel_casing",
            new CasingBlock(copyProperties(KlaxonBlocks.STEEL_BLOCK)));
    public static final Holder<Block> STEEL_WIRE_SPOOL_BLOCK = register("steel_wire_spool",
            new RotatedPillarBlock(copyProperties(STEEL_BLOCK)));
    public static final Holder<Block> STEEL_DOOR = register("steel_door",
            new SteelDoorBlock(KlaxonBlockSetTypes.STEEL, copyProperties(STEEL_BLOCK).noOcclusion()));
    public static final Holder<Block> STEEL_TRAPDOOR = register("steel_trapdoor",
            new SteelTrapdoorBlock(KlaxonBlockSetTypes.STEEL, copyProperties(STEEL_DOOR)));
    public static final Holder<Block> HEAVY_GATED_PRESSURE_PLATE = register(
            "heavy_gated_pressure_plate",
            new HeavyGatedPressurePlateBlock(
                    copyProperties(STEEL_CASING)
                            .noCollission()
                            .forceSolidOn()
                            .pushReaction(PushReaction.DESTROY)
                            .requiresCorrectToolForDrops(),
                    KlaxonBlockSetTypes.STEEL)
    );


    // crude steel
    public static final Holder<Block> CRUDE_STEEL_BLOCK = register(
            "crude_steel_block",
            new Block(
                    copyProperties(Blocks.IRON_BLOCK)
                            .pushReaction(PushReaction.DESTROY)
                            .strength(2.5f, 3.0f)
            )
    );
    public static final Holder<Block> CRUDE_STEEL_PLATING_BLOCK = register("crude_steel_plating_block",
            new RotatedPillarBlock(copyProperties(CRUDE_STEEL_BLOCK)));
    public static final Holder<Block> CRUDE_STEEL_CASING = register("crude_steel_casing",
            new CasingBlock(copyProperties(KlaxonBlocks.CRUDE_STEEL_BLOCK)));
    public static final Holder<Block> CRUDE_STEEL_DOOR = register("crude_steel_door",
            new SteelDoorBlock(KlaxonBlockSetTypes.CRUDE_STEEL, copyProperties(CRUDE_STEEL_BLOCK).noOcclusion()));
    public static final Holder<Block> CRUDE_STEEL_TRAPDOOR = register("crude_steel_trapdoor",
            new SteelTrapdoorBlock(KlaxonBlockSetTypes.CRUDE_STEEL, copyProperties(CRUDE_STEEL_DOOR)));
    public static final Holder<Block> FAULTY_HEAVY_GATED_PRESSURE_PLATE = register(
            "faulty_heavy_gated_pressure_plate",
            new FaultyHeavyGatedPressurePlateBlock(
                    copyProperties(CRUDE_STEEL_CASING)
                            .forceSolidOn()
                            .noCollission()
                            .requiresCorrectToolForDrops(),
                    KlaxonBlockSetTypes.CRUDE_STEEL)
    );

    // machines
    public static final Holder<Block> DEEPSLATE_BLAST_PROCESSOR = register("deepslate_blast_processor",
            new DeepslateBlastProcessorBlock(copyProperties(Blocks.POLISHED_DEEPSLATE).lightLevel(Blocks.litBlockEmission(15))));
    public static final Holder<Block> STEEL_BLAST_PROCESSOR = register("steel_blast_processor",
            new SteelBlastProcessorBlock(copyProperties(STEEL_CASING)));
    public static final Holder<Block> PRECISION_DISPENSER = register("precision_dispenser",
            new PrecisionDispenserBlock(copyProperties(STEEL_CASING)));
    public static final Holder<Block> NETHER_REACTOR_CORE = register("nether_reactor_core",
            new NetherReactorCoreBlock(copyProperties(KlaxonBlocks.STEEL_CASING).lightLevel((state) -> 15).noOcclusion()));
    public static final Holder<Block> CRUDE_NETHER_REACTOR_CORE = register("crude_nether_reactor_core",
            new NetherReactorCoreBlock(copyProperties(KlaxonBlocks.CRUDE_STEEL_CASING).lightLevel((state) -> 12).noOcclusion()));
    public static final Holder<Block> MODULAR_EXPLOSIVE_BLOCK = register("modular_explosive_block",
            new ModularExplosiveBlock(copyProperties(STEEL_CASING)));

    // filing cabinets
    public static final Holder<Block> FILING_CABINET_DRAWER = registerFilingCabinetDrawer("filing_cabinet_drawer", null);
    public static final Holder<Block> WHITE_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("white_filing_cabinet_drawer", DyeColor.WHITE);
    public static final Holder<Block> ORANGE_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("orange_filing_cabinet_drawer", DyeColor.ORANGE);
    public static final Holder<Block> MAGENTA_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("magenta_filing_cabinet_drawer", DyeColor.MAGENTA);
    public static final Holder<Block> LIGHT_BLUE_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("light_blue_filing_cabinet_drawer", DyeColor.LIGHT_BLUE);
    public static final Holder<Block> YELLOW_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("yellow_filing_cabinet_drawer", DyeColor.YELLOW);
    public static final Holder<Block> LIME_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("lime_filing_cabinet_drawer", DyeColor.LIME);
    public static final Holder<Block> PINK_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("pink_filing_cabinet_drawer", DyeColor.PINK);
    public static final Holder<Block> GRAY_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("gray_filing_cabinet_drawer", DyeColor.GRAY);
    public static final Holder<Block> LIGHT_GRAY_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("light_gray_filing_cabinet_drawer", DyeColor.LIGHT_GRAY);
    public static final Holder<Block> CYAN_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("cyan_filing_cabinet_drawer", DyeColor.CYAN);
    public static final Holder<Block> PURPLE_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("purple_filing_cabinet_drawer", DyeColor.PURPLE);
    public static final Holder<Block> BLUE_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("blue_filing_cabinet_drawer", DyeColor.BLUE);
    public static final Holder<Block> BROWN_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("brown_filing_cabinet_drawer", DyeColor.BROWN);
    public static final Holder<Block> GREEN_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("green_filing_cabinet_drawer", DyeColor.GREEN);
    public static final Holder<Block> RED_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("red_filing_cabinet_drawer", DyeColor.RED);
    public static final Holder<Block> BLACK_FILING_CABINET_DRAWER = registerFilingCabinetDrawer("black_filing_cabinet_drawer", DyeColor.BLACK);

    public static final Holder<Block> FILING_CABINET_BASE = registerFilingCabinetBase("filing_cabinet_base", FILING_CABINET_DRAWER);
    public static final Holder<Block> WHITE_FILING_CABINET_BASE = registerFilingCabinetBase("white_filing_cabinet_base", WHITE_FILING_CABINET_DRAWER);
    public static final Holder<Block> ORANGE_FILING_CABINET_BASE = registerFilingCabinetBase("orange_filing_cabinet_base", ORANGE_FILING_CABINET_DRAWER);
    public static final Holder<Block> MAGENTA_FILING_CABINET_BASE = registerFilingCabinetBase("magenta_filing_cabinet_base", MAGENTA_FILING_CABINET_DRAWER);
    public static final Holder<Block> LIGHT_BLUE_FILING_CABINET_BASE = registerFilingCabinetBase("light_blue_filing_cabinet_base", LIGHT_BLUE_FILING_CABINET_DRAWER);
    public static final Holder<Block> YELLOW_FILING_CABINET_BASE = registerFilingCabinetBase("yellow_filing_cabinet_base", YELLOW_FILING_CABINET_DRAWER);
    public static final Holder<Block> LIME_FILING_CABINET_BASE = registerFilingCabinetBase("lime_filing_cabinet_base", LIME_FILING_CABINET_DRAWER);
    public static final Holder<Block> PINK_FILING_CABINET_BASE = registerFilingCabinetBase("pink_filing_cabinet_base", PINK_FILING_CABINET_DRAWER);
    public static final Holder<Block> GRAY_FILING_CABINET_BASE = registerFilingCabinetBase("gray_filing_cabinet_base", GRAY_FILING_CABINET_DRAWER);
    public static final Holder<Block> LIGHT_GRAY_FILING_CABINET_BASE = registerFilingCabinetBase("light_gray_filing_cabinet_base", LIGHT_GRAY_FILING_CABINET_DRAWER);
    public static final Holder<Block> CYAN_FILING_CABINET_BASE = registerFilingCabinetBase("cyan_filing_cabinet_base", CYAN_FILING_CABINET_DRAWER);
    public static final Holder<Block> PURPLE_FILING_CABINET_BASE = registerFilingCabinetBase("purple_filing_cabinet_base", PURPLE_FILING_CABINET_DRAWER);
    public static final Holder<Block> BLUE_FILING_CABINET_BASE = registerFilingCabinetBase("blue_filing_cabinet_base", BLUE_FILING_CABINET_DRAWER);
    public static final Holder<Block> BROWN_FILING_CABINET_BASE = registerFilingCabinetBase("brown_filing_cabinet_base", BROWN_FILING_CABINET_DRAWER);
    public static final Holder<Block> GREEN_FILING_CABINET_BASE = registerFilingCabinetBase("green_filing_cabinet_base", GREEN_FILING_CABINET_DRAWER);
    public static final Holder<Block> RED_FILING_CABINET_BASE = registerFilingCabinetBase("red_filing_cabinet_base", RED_FILING_CABINET_DRAWER);
    public static final Holder<Block> BLACK_FILING_CABINET_BASE = registerFilingCabinetBase("black_filing_cabinet_base", BLACK_FILING_CABINET_DRAWER);

    // item ducts
    public static final Holder<Block> DUCT_SEGMENT = register("duct_segment",
            new DuctSegmentBlock(copyProperties(Blocks.IRON_BLOCK).pushReaction(PushReaction.DESTROY))
    );
    public static final Holder<Block> AIO_DUCT_DRIVER = register("all_in_one_duct_driver",
            new AIODuctDriverBlock(copyProperties(Blocks.IRON_BLOCK).pushReaction(PushReaction.DESTROY))
    );

    // workstations
    public static final Holder<Block> STEEL_WORKBENCH = register(
            "steel_workbench",
            new SteelWorkbenchBlock(copyProperties(KlaxonBlocks.STEEL_CASING))
    );

    // pipe matrices
    public static final Holder<Block> COPPER_PIPE_MATRIX_U_BEND = register("copper_pipe_matrix_u_bend",
            new OxidizablePipeMatrixUBendBlock(
                    WeatheringCopper.WeatherState.UNAFFECTED,
                    copyProperties(Blocks.COPPER_BLOCK)
            )
    );
    public static final Holder<Block> COPPER_PIPE_MATRIX_SEGMENT = register("copper_pipe_matrix_segment",
            new OxidizablePipeMatrixSegmentBlock(
                    WeatheringCopper.WeatherState.UNAFFECTED,
                    copyProperties(COPPER_PIPE_MATRIX_U_BEND),
                    COPPER_PIPE_MATRIX_U_BEND.value()
            )
    );
    public static final Holder<Block> EXPOSED_COPPER_PIPE_MATRIX_U_BEND = register("exposed_copper_pipe_matrix_u_bend",
            new OxidizablePipeMatrixUBendBlock(
                    WeatheringCopper.WeatherState.EXPOSED,
                    copyProperties(Blocks.EXPOSED_COPPER)
            )
    );
    public static final Holder<Block> EXPOSED_COPPER_PIPE_MATRIX_SEGMENT = register("exposed_copper_pipe_matrix_segment",
            new OxidizablePipeMatrixSegmentBlock(
                    WeatheringCopper.WeatherState.EXPOSED,
                    copyProperties(EXPOSED_COPPER_PIPE_MATRIX_U_BEND),
                    EXPOSED_COPPER_PIPE_MATRIX_U_BEND.value()
            )
    );
    public static final Holder<Block> WEATHERED_COPPER_PIPE_MATRIX_U_BEND = register("weathered_copper_pipe_matrix_u_bend",
            new OxidizablePipeMatrixUBendBlock(
                    WeatheringCopper.WeatherState.WEATHERED,
                    copyProperties(Blocks.WEATHERED_COPPER)
            )
    );
    public static final Holder<Block> WEATHERED_COPPER_PIPE_MATRIX_SEGMENT = register("weathered_copper_pipe_matrix_segment",
            new OxidizablePipeMatrixSegmentBlock(
                    WeatheringCopper.WeatherState.WEATHERED,
                    copyProperties(WEATHERED_COPPER_PIPE_MATRIX_U_BEND),
                    WEATHERED_COPPER_PIPE_MATRIX_U_BEND.value()
            )
    );
    public static final Holder<Block> OXIDIZED_COPPER_PIPE_MATRIX_U_BEND = register("oxidized_copper_pipe_matrix_u_bend",
            new OxidizablePipeMatrixUBendBlock(
                    WeatheringCopper.WeatherState.OXIDIZED,
                    copyProperties(Blocks.OXIDIZED_COPPER)
            )
    );
    public static final Holder<Block> OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT = register("oxidized_copper_pipe_matrix_segment",
            new OxidizablePipeMatrixSegmentBlock(
                    WeatheringCopper.WeatherState.OXIDIZED,
                    copyProperties(OXIDIZED_COPPER_PIPE_MATRIX_U_BEND),
                    OXIDIZED_COPPER_PIPE_MATRIX_U_BEND.value()
            )
    );
    public static final Holder<Block> WAXED_COPPER_PIPE_MATRIX_U_BEND = register("waxed_copper_pipe_matrix_u_bend",
            new PipeMatrixUBendBlock(
                    copyProperties(Blocks.WAXED_COPPER_BLOCK)
            )
    );
    public static final Holder<Block> WAXED_COPPER_PIPE_MATRIX_SEGMENT = register("waxed_copper_pipe_matrix_segment",
            new PipeMatrixSegmentBlock(
                    copyProperties(WAXED_COPPER_PIPE_MATRIX_U_BEND),
                    WAXED_COPPER_PIPE_MATRIX_U_BEND.value()
            )
    );
    public static final Holder<Block> WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND = register("waxed_exposed_copper_pipe_matrix_u_bend",
            new PipeMatrixUBendBlock(
                    copyProperties(Blocks.WAXED_EXPOSED_COPPER)
            )
    );
    public static final Holder<Block> WAXED_EXPOSED_COPPER_PIPE_MATRIX_SEGMENT = register("waxed_exposed_copper_pipe_matrix_segment",
            new PipeMatrixSegmentBlock(
                    copyProperties(WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND),
                    WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND.value()
            )
    );
    public static final Holder<Block> WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND = register("waxed_weathered_copper_pipe_matrix_u_bend",
            new PipeMatrixUBendBlock(
                    copyProperties(Blocks.WAXED_WEATHERED_COPPER)
            )
    );
    public static final Holder<Block> WAXED_WEATHERED_COPPER_PIPE_MATRIX_SEGMENT = register("waxed_weathered_copper_pipe_matrix_segment",
            new PipeMatrixSegmentBlock(
                    copyProperties(WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND),
                    WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND.value()
            )
    );
    public static final Holder<Block> WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND = register("waxed_oxidized_copper_pipe_matrix_u_bend",
            new PipeMatrixUBendBlock(
                    copyProperties(Blocks.WAXED_OXIDIZED_COPPER)
            )
    );
    public static final Holder<Block> WAXED_OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT = register("waxed_oxidized_copper_pipe_matrix_segment",
            new PipeMatrixSegmentBlock(
                    copyProperties(WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND),
                    WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND.value()
            )
    );


    // hallnox
    public static final Holder<Block> HALLNOX_POD = register("hallnox_pod",
            new HallnoxPodBlock(
                    KlaxonSaplingGenerators.HALLNOX,
                    copyProperties(Blocks.SHROOMLIGHT).lightLevel((state) -> 12)
            )
    );
    public static final Holder<Block> POTTED_HALLNOX_POD = register("potted_hallnox_pod",
            new FlowerPotBlock(
                    HALLNOX_POD.value(),
                    BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).lightLevel((state) -> 12)
            )
    );
    public static final Holder<Block> HALLNOX_WART_BLOCK = register("hallnox_wart_block",
            new Block(copyProperties(Blocks.WARPED_WART_BLOCK).mapColor(MapColor.TERRACOTTA_GRAY).strength(1.0f, 2.0f))
    );
    public static final Holder<Block> HALLNOX_STEM = register("hallnox_stem",
            new RotatedPillarBlock(copyProperties(Blocks.WARPED_STEM).mapColor(MapColor.TERRACOTTA_GRAY).strength(3.0f, 4.0f))
    );
    public static final Holder<Block> STRIPPED_HALLNOX_STEM = register("stripped_hallnox_stem",
            new RotatedPillarBlock(copyProperties(HALLNOX_STEM))
    );
    public static final Holder<Block> HALLNOX_HYPHAE = register("hallnox_hyphae",
            new RotatedPillarBlock(copyProperties(HALLNOX_STEM))
    );
    public static final Holder<Block> STRIPPED_HALLNOX_HYPHAE = register("stripped_hallnox_hyphae",
            new RotatedPillarBlock(copyProperties(HALLNOX_STEM))
    );
    public static final Holder<Block> HALLNOX_PLANKS = register("hallnox_planks",
            new Block(copyProperties(Blocks.WARPED_PLANKS).mapColor(MapColor.TERRACOTTA_GRAY))
    );
    public static final Holder<Block> HALLNOX_STAIRS = register("hallnox_stairs",
            new StairBlock(HALLNOX_PLANKS.value().defaultBlockState(), copyProperties(HALLNOX_PLANKS))
    );
    public static final Holder<Block> HALLNOX_SLAB = register("hallnox_slab",
            new SlabBlock(copyProperties(HALLNOX_PLANKS))
    );
    public static final Holder<Block> HALLNOX_PRESSURE_PLATE = register("hallnox_pressure_plate",
            new PressurePlateBlock(KlaxonBlockSetTypes.HALLNOX, copyProperties(Blocks.WARPED_PRESSURE_PLATE)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(1.0f))
    );
    public static final Holder<Block> HALLNOX_BUTTON = register("hallnox_button",
            new ButtonBlock(KlaxonBlockSetTypes.HALLNOX, 40, copyProperties(Blocks.WARPED_BUTTON)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(1.0f))
    );
    public static final Holder<Block> HALLNOX_TRAPDOOR = register("hallnox_trapdoor",
            new TrapDoorBlock(KlaxonBlockSetTypes.HALLNOX, copyProperties(Blocks.WARPED_TRAPDOOR)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Holder<Block> HALLNOX_DOOR = register("hallnox_door",
            new DoorBlock(KlaxonBlockSetTypes.HALLNOX, copyProperties(Blocks.WARPED_DOOR)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Holder<Block> HALLNOX_SIGN = register("hallnox_sign",
            new StandingSignBlock(KlaxonWoodTypes.HALLNOX, copyProperties(Blocks.WARPED_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
            )
    );
    public static final Holder<Block> HALLNOX_WALL_SIGN = register("hallnox_wall_sign",
            new WallSignBlock(KlaxonWoodTypes.HALLNOX, copyProperties(Blocks.WARPED_WALL_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
                    .dropsLike(HALLNOX_SIGN.value())
            )
    );
    public static final Holder<Block> HALLNOX_HANGING_SIGN = register("hallnox_hanging_sign",
            new CeilingHangingSignBlock(KlaxonWoodTypes.HALLNOX, copyProperties(Blocks.WARPED_HANGING_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
            )
    );
    public static final Holder<Block> HALLNOX_WALL_HANGING_SIGN = register("hallnox_wall_hanging_sign",
            new WallHangingSignBlock(KlaxonWoodTypes.HALLNOX, copyProperties(Blocks.WARPED_WALL_HANGING_SIGN)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(2.0f, 3.0f)
                    .dropsLike(HALLNOX_HANGING_SIGN.value()))
    );
    public static final Holder<Block> HALLNOX_FENCE = register("hallnox_fence",
            new FenceBlock(copyProperties(Blocks.WARPED_FENCE)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Holder<Block> HALLNOX_FENCE_GATE = register("hallnox_fence_gate",
            new FenceGateBlock(KlaxonWoodTypes.HALLNOX, copyProperties(Blocks.WARPED_FENCE)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .strength(3.0f, 4.0f))
    );
    public static final Holder<Block> HALLNOX_BULB = register("hallnox_bulb",
            new HallnoxBulbBlock(copyProperties(Blocks.GLASS).lightLevel((state) -> 15))
    );


    // iron
    public static final Holder<Block> IRON_PLATING_BLOCK = register("iron_plating_block",
            new RotatedPillarBlock(copyProperties(Blocks.IRON_BLOCK)));
    public static final Holder<Block> IRON_WIRE_SPOOL_BLOCK = register("iron_wire_spool",
            new RotatedPillarBlock(copyProperties(Blocks.IRON_BLOCK)));

    // gold
    public static final Holder<Block> GOLD_PLATING_BLOCK = register("gold_plating_block",
            new RotatedPillarBlock(copyProperties(Blocks.GOLD_BLOCK)));
    public static final Holder<Block> GOLD_WIRE_SPOOL_BLOCK = register("gold_wire_spool",
            new RotatedPillarBlock(copyProperties(Blocks.GOLD_BLOCK)));

    // copper plating blocks
    public static final Holder<Block> COPPER_PLATING_BLOCK = register("copper_plating_block",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.UNAFFECTED, copyProperties(Blocks.COPPER_BLOCK)));
    public static final Holder<Block> EXPOSED_COPPER_PLATING_BLOCK = register("exposed_copper_plating_block",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.EXPOSED, copyProperties(Blocks.EXPOSED_COPPER)));
    public static final Holder<Block> WEATHERED_COPPER_PLATING_BLOCK = register("weathered_copper_plating_block",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.WEATHERED, copyProperties(Blocks.WEATHERED_COPPER)));
    public static final Holder<Block> OXIDIZED_COPPER_PLATING_BLOCK = register("oxidized_copper_plating_block",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.OXIDIZED, copyProperties(Blocks.OXIDIZED_COPPER)));
    public static final Holder<Block> WAXED_COPPER_PLATING_BLOCK = register("waxed_copper_plating_block",
            new RotatedPillarBlock(copyProperties(Blocks.COPPER_BLOCK)));
    public static final Holder<Block> WAXED_EXPOSED_COPPER_PLATING_BLOCK = register("waxed_exposed_copper_plating_block",
            new RotatedPillarBlock(copyProperties(Blocks.EXPOSED_COPPER)));
    public static final Holder<Block> WAXED_WEATHERED_COPPER_PLATING_BLOCK = register("waxed_weathered_copper_plating_block",
            new RotatedPillarBlock(copyProperties(Blocks.WEATHERED_COPPER)));
    public static final Holder<Block> WAXED_OXIDIZED_COPPER_PLATING_BLOCK = register("waxed_oxidized_copper_plating_block",
            new RotatedPillarBlock(copyProperties(Blocks.OXIDIZED_COPPER)));
    // copper wire spool
    public static final Holder<Block> COPPER_WIRE_SPOOL_BLOCK = register("copper_wire_spool",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.UNAFFECTED, copyProperties(Blocks.COPPER_BLOCK)));
    public static final Holder<Block> EXPOSED_COPPER_WIRE_SPOOL_BLOCK = register("exposed_copper_wire_spool",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.EXPOSED, copyProperties(Blocks.EXPOSED_COPPER)));
    public static final Holder<Block> WEATHERED_COPPER_WIRE_SPOOL_BLOCK = register("weathered_copper_wire_spool",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.WEATHERED, copyProperties(Blocks.WEATHERED_COPPER)));
    public static final Holder<Block> OXIDIZED_COPPER_WIRE_SPOOL_BLOCK = register("oxidized_copper_wire_spool",
            new OxidizablePillarBlock(WeatheringCopper.WeatherState.OXIDIZED, copyProperties(Blocks.OXIDIZED_COPPER)));
    public static final Holder<Block> WAXED_COPPER_WIRE_SPOOL_BLOCK = register("waxed_copper_wire_spool",
            new RotatedPillarBlock(copyProperties(Blocks.WAXED_COPPER_BLOCK)));
    public static final Holder<Block> WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK = register("waxed_exposed_copper_wire_spool",
            new RotatedPillarBlock(copyProperties(Blocks.WAXED_EXPOSED_COPPER)));
    public static final Holder<Block> WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK = register("waxed_weathered_copper_wire_spool",
            new RotatedPillarBlock(copyProperties(Blocks.WAXED_WEATHERED_COPPER)));
    public static final Holder<Block> WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK = register("waxed_oxidized_copper_wire_spool",
            new RotatedPillarBlock(copyProperties(Blocks.WAXED_OXIDIZED_COPPER)));

    // rubber
    public static final Holder<Block> RUBBER_BLOCK = register("rubber_block",
            new Block(copyProperties(Blocks.SHROOMLIGHT)
                    .sound(SoundType.SHROOMLIGHT)
                    .mapColor(MapColor.CRIMSON_HYPHAE)
                    .strength(3.0f)
                    .lightLevel((state) -> 0)));
    public static final Holder<Block> RUBBER_SHEET_BLOCK = register("rubber_sheet_block",
            new RotatedPillarBlock(copyProperties(RUBBER_BLOCK)));

    // molten rubber
    public static final Holder<Block> MOLTEN_RUBBER_BLOCK = register("molten_rubber_block",
            new MoltenRubberBlock(copyProperties(RUBBER_BLOCK)
                    .lightLevel(state -> 3)
                    .emissiveRendering(Blocks::always)
                    .isValidSpawn((state, world, pos, entityType) -> entityType.fireImmune())
                    .hasPostProcess(Blocks::always)
                    .mapColor(MapColor.NETHER)
                    .strength(1.5F)));

    private static BlockBehaviour.Properties copyProperties(Holder<Block> holder) {
        return copyProperties(holder.value());
    }

    private static BlockBehaviour.Properties copyProperties(Block block) {
        return BlockBehaviour.Properties.ofFullCopy(block);
    }

    private static Holder<Block> registerFilingCabinetDrawer(String name, @Nullable DyeColor color) {
        return register(name, new FilingCabinetDrawerBlock(copyProperties(Blocks.IRON_BLOCK).pushReaction(PushReaction.BLOCK).mapColor(color == null ? MapColor.METAL : color.getMapColor()), color));
    }

    private static Holder<Block> registerFilingCabinetBase(String name, Holder<Block> drawerBlockHolder) {
        return register(name, new FilingCabinetBaseBlock(copyProperties(drawerBlockHolder.value()), drawerBlockHolder, ((FilingCabinetDrawerBlock) drawerBlockHolder.value()).getDyeColor()));
    }

    private static Holder<Block> register(String name, Block block) {
        return Registry.registerForHolder(BuiltInRegistries.BLOCK, KlaxonCommon.locate(name), block);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blocks!");
    }
}
