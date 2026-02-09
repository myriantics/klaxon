package net.myriantics.klaxon.registry.behavior;

import net.minecraft.block.enums.Orientation;
import net.minecraft.block.enums.RailShape;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.behaviors.*;
import net.myriantics.klaxon.registry.KlaxonRegistries;

import java.util.function.Function;

public abstract class KlaxonBlockStateWrenchBehaviors {
    public static final BlockStateWrenchBehavior<Direction> FACING = register("facing", FacingBlockStateWrenchBehavior::new);
    public static final BlockStateWrenchBehavior<Direction> HORIZONTAL_FACING = register("horizontal_facing", HorizontalFacingBlockStateWrenchBehavior::new);
    public static final BlockStateWrenchBehavior<Direction> HOPPER_FACING = register("hopper_facing", HopperFacingBlockStateWrenchBehavior::new);
    public static final BlockStateWrenchBehavior<Direction.Axis> AXIS = register("axis", AxisBlockStateWrenchBehavior::new);
    public static final BlockStateWrenchBehavior<Direction.Axis> HORIZONTAL_AXIS = register("horizontal_axis", HorizontalAxisBlockStateWrenchBehavior::new);
    public static final BlockStateWrenchBehavior<Orientation> ORIENTATION = register("orientation", OrientationBlockStateWrenchBehavior::new);
    public static final BlockStateWrenchBehavior<RailShape> CURVING_RAIL_SHAPE = register("curving_rail_shape", CurvingRailShapeBlockStateWrenchBehavior::new);
    public static final BlockStateWrenchBehavior<RailShape> STRAIGHT_RAIL_SHAPE = register("straight_rail_shape", StraightRailShapeBlockStateWrenchBehavior::new);

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Block State Wrench Behaviors!");
    }

    private static <T extends Comparable<T>> BlockStateWrenchBehavior<T> register(String name, Function<Identifier, BlockStateWrenchBehavior<T>> constructor) {
        Identifier id = KlaxonCommon.locate(name);
        return Registry.register(KlaxonRegistries.BLOCK_STATE_WRENCH_BEHAVIORS, KlaxonCommon.locate(name), constructor.apply(id));
    }
}
