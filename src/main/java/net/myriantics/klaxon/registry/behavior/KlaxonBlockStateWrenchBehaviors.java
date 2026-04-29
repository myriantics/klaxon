package net.myriantics.klaxon.registry.behavior;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.behaviors.*;
import net.myriantics.klaxon.registry.KlaxonRegistries;

import java.util.function.Function;

public abstract class KlaxonBlockStateWrenchBehaviors {
    public static final Holder<BlockStateWrenchBehavior<Direction>> FACING = register("facing", FacingBlockStateWrenchBehavior::new);
    public static final Holder<BlockStateWrenchBehavior<Direction>> HORIZONTAL_FACING = register("horizontal_facing", HorizontalFacingBlockStateWrenchBehavior::new);
    public static final Holder<BlockStateWrenchBehavior<Direction>> HOPPER_FACING = register("hopper_facing", HopperFacingBlockStateWrenchBehavior::new);
    public static final Holder<BlockStateWrenchBehavior<Direction.Axis>> AXIS = register("axis", AxisBlockStateWrenchBehavior::new);
    public static final Holder<BlockStateWrenchBehavior<Direction.Axis>> HORIZONTAL_AXIS = register("horizontal_axis", HorizontalAxisBlockStateWrenchBehavior::new);
    public static final Holder<BlockStateWrenchBehavior<FrontAndTop>> ORIENTATION = register("orientation", FrontAndTopBlockStateWrenchBehavior::new);
    public static final Holder<BlockStateWrenchBehavior<RailShape>> CURVING_RAIL_SHAPE = register("curving_rail_shape", CurvingRailShapeBlockStateWrenchBehavior::new);
    public static final Holder<BlockStateWrenchBehavior<RailShape>> STRAIGHT_RAIL_SHAPE = register("straight_rail_shape", StraightRailShapeBlockStateWrenchBehavior::new);

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Block State Wrench Behaviors!");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> Holder<BlockStateWrenchBehavior<T>> register(String name, Function<ResourceLocation, BlockStateWrenchBehavior<T>> constructor) {
        ResourceLocation id = KlaxonCommon.locate(name);
        return (Holder<BlockStateWrenchBehavior<T>>) (Object) Registry.registerForHolder(KlaxonRegistries.BLOCK_STATE_WRENCH_BEHAVIORS, KlaxonCommon.locate(name), constructor.apply(id));
    }
}
