package net.myriantics.klaxon.registry.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.myriantics.klaxon.block.functional.pressure_plate.FaultyHeavyGatedPressurePlateBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorLootState;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.UBendRotation;
import net.myriantics.klaxon.block.machines.modular_explosive.FuseState;

public abstract class KlaxonBlockStateProperties {

    // Used to display internal slot status of the Deepslate Blast Processor
    public static final EnumProperty<DeepslateBlastProcessorLootState> DEEPSLATE_BLAST_PROCESSOR_LOOT_STATE = EnumProperty.create("loot_state", DeepslateBlastProcessorLootState.class);

    // Used to sync basic muffling status between client and server
    public static final BooleanProperty MUFFLED = BooleanProperty.create("muffled");

    // Used by the Hallnox Pod to disable growth when interacted with using shears.
    public static final BooleanProperty GROWTH_DISABLED = BooleanProperty.create("growth_disabled");

    // Used by Pipe Matrices to indicate whether they're a valid structure for Geothermal Power Generation or not.
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public static final EnumProperty<UBendRotation> U_BEND_ROTATION = EnumProperty.create("u_bend_rotation", UBendRotation.class);

    // Used by Modular Explosives to telegraph their fuse time
    public static final EnumProperty<FuseState> FUSE = EnumProperty.create("fuse", FuseState.class);

    public static final EnumProperty<FaultyHeavyGatedPressurePlateBlock.State> FAULTY_HEAVY_GATED_PRESSURE_PLATE_STATE = EnumProperty.create("press_state", FaultyHeavyGatedPressurePlateBlock.State.class);

    // AIO duct driver
    public static final DirectionProperty POWER_SOCKET_FACING = DirectionProperty.create("power_socket_facing");
}
