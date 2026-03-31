package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.wrench.*;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.mechanics.wrench.interaction.segments.InteractionMapSegment;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.util.BlockFaceRegion;
import net.myriantics.klaxon.util.RelativeDirection;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class CurvingRailShapeBlockStateWrenchBehavior extends BlockStateWrenchBehavior<RailShape> {

    private final WrenchInteraction ALIGN = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ALIGN,
            this::align
    );
    private final WrenchInteraction RAISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.RAISE,
            this::toggleAscension
    );
    private final WrenchInteraction LOWER = WrenchInteraction.of(
            KlaxonWrenchActionTypes.LOWER,
            this::toggleAscension
    );
    private final WrenchInteraction CURVE_LEFT = WrenchInteraction.of(
            KlaxonWrenchActionTypes.CURVE_LEFT,
            (context, rotation) -> this.curve(context, rotation, RelativeDirection.LEFT)
    );
    private final WrenchInteraction CURVE_RIGHT = WrenchInteraction.of(
            KlaxonWrenchActionTypes.CURVE_RIGHT,
            (context, rotation) -> this.curve(context, rotation, RelativeDirection.RIGHT)
    );
    private final WrenchInteraction ROTATE_CLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_CLOCKWISE,
            (context, rotation) -> this.rotateSimple(context, KlaxonRailHelper::rotateClockwise)
    );
    private final WrenchInteraction ROTATE_COUNTERCLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_COUNTERCLOCKWISE,
            (context, rotation) -> this.rotateSimple(context, KlaxonRailHelper::rotateCounterClockwise)
    );

    private final WrenchInteractionMap ALIGN_MAP = WrenchInteractionMap.fullBlock(ALIGN);

    private final WrenchInteractionMap LOWER_MAP = WrenchInteractionMap.fullBlock(LOWER);
    private final WrenchInteractionMap CURVE = WrenchInteractionMap.splitVertical(
            CURVE_LEFT,
            CURVE_RIGHT
    ).state2Rotation(BlockFaceRegion.State2Rotation::topOnly);
    private final WrenchInteractionMap RAISE_OR_CURVE = WrenchInteractionMap.create()
            .add(InteractionMapSegment.of(RAISE, 0, 6f/16, 1, 1))
            .add(InteractionMapSegment.of(CURVE_LEFT, 0, 0, 0.5f, 6f/16))
            .add(InteractionMapSegment.of(CURVE_RIGHT, 0.5f, 0, 1, 6f/16))
            .state2Rotation(BlockFaceRegion.State2Rotation::topOnly);
    public CurvingRailShapeBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.RAIL_SHAPE, id);
    }

    private Optional<InteractionResult> toggleAscension(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        RailShape oldShape = state.getValue(this.getProperty());
        @Nullable Direction.Axis axis = KlaxonRailHelper.railShapeToAxis(oldShape);

        @Nullable RailShape newShape = KlaxonRailHelper.tryToggleAscending(
                level,
                oldShape,
                pos,
                switch (context) {
                    case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing().getAxisDirection().opposite();
                    case WrenchActionContext.Manual manual -> manual.getPlayer().getMotionDirection().getAxisDirection();
                });

        if (axis == null || newShape == null) {
            return Optional.of(InteractionResult.FAIL);
        } else {
            this.updateState(level, pos, state.setValue(this.getProperty(), newShape), context.getUser());
            return Optional.of(InteractionResult.SUCCESS);
        }
    }

    private Optional<InteractionResult> curve(WrenchActionContext context, BlockFaceRegion.Rotation rotation, RelativeDirection curveDir) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();

        Property<RailShape> property = this.getProperty();
        RailShape original = state.getValue(property);

        Direction.AxisDirection userFacingAxisDir = switch (context) {
            case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing().getAxisDirection();
            case WrenchActionContext.Manual manual -> manual.getPlayer().getMotionDirection().getAxisDirection();
        };

        @Nullable RailShape newShape = KlaxonRailHelper.tryCurvingRail(original, userFacingAxisDir, curveDir);
        if (newShape != null && !original.equals(newShape)) {
            this.updateState(level, pos, state.setValue(property, newShape), context.getUser());
            return Optional.of(InteractionResult.SUCCESS);
        } else {
            return Optional.of(InteractionResult.FAIL);
        }
    }

    private Optional<InteractionResult> align(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();

        Direction userFacing = switch (context) {
            case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing();
            case WrenchActionContext.Manual manual -> manual.getPlayer().getMotionDirection();
        };

        Property<RailShape> property = this.getProperty();
        RailShape oldShape = state.getValue(property);
        RailShape newShape = KlaxonRailHelper.axisToRailShape(userFacing.getAxis());

        if (oldShape.equals(newShape)) {
            return Optional.of(InteractionResult.FAIL);
        } else {
            BlockState newState = state.setValue(property, newShape);
            this.updateState(level, pos, newState, context.getUser());
            return Optional.of(InteractionResult.SUCCESS);
        }
    }

    private Optional<InteractionResult> rotateSimple(WrenchActionContext context, UnaryOperator<RailShape> operator) {
        // giga one liner of doom
        context.level().setBlockAndUpdate(context.getTargetPos(), context.getTargetState().setValue(this.getProperty(), operator.apply(context.getTargetState().getValue(this.getProperty()))));
        return Optional.of(InteractionResult.SUCCESS);
    }

    private void updateState(Level level, BlockPos pos, BlockState newState, @Nullable Entity user) {
        level.setBlockAndUpdate(pos, newState);
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        Direction horizFacing = context.getUser().getMotionDirection();
        Level level = context.level();
        BlockState targetState = context.getTargetState();
        BlockPos targetPos = context.getTargetPos();

        RailShape shape = targetState.getValue(this.getProperty());
        @Nullable Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(shape);


        if (shape.isAscending()) { // try to lower first
            return LOWER_MAP;
        } else if (horizFacing.getAxis().equals(railAxis)) { // if aligned, raise or curve
            if (KlaxonRailHelper.canAscend(level, shape, targetPos, horizFacing)) {
                return RAISE_OR_CURVE;
            } else {
                return CURVE;
            }
        } else { // if all else fails, align
            return ALIGN_MAP;
        }
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        Direction dispenserFacing = context.getDispenserFacing();
        BlockState state = context.getTargetState();
        RailShape shape = state.getValue(this.getProperty());

        if (shape.isAscending()) {
            return LOWER;
        }

        return switch (dispenserFacing) {
            case DOWN -> ROTATE_CLOCKWISE;
            case UP -> ROTATE_COUNTERCLOCKWISE;
            case NORTH, SOUTH, EAST, WEST -> {
                @Nullable Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(shape);
                if (railAxis == null || !railAxis.equals(dispenserFacing.getAxis())) {
                    yield ALIGN;
                } else {
                    yield RAISE;
                }
            }
        };
    }

    @Override
    protected Optional<RailShape> applyDispenser(RailShape original, DispenserWrenchInteractionContext context) {
        Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(original);
        Direction.Axis dispenserAxis = context.dispenserFacing().getAxis();

        switch (context.dispenserFacing()) {
            case DOWN, UP -> {
                // start off with making the rail flat if needed
                if (original.isAscending()) {
                    @Nullable RailShape toggled = KlaxonRailHelper.tryToggleAscending(context.serverWorld(), original, context.targetPos(), context.dispenserFacing().getOpposite().getAxisDirection());
                    if (toggled != null && !toggled.equals(original)) {
                        return Optional.of(toggled);
                    }
                }

                @Nullable RailShape rotated = KlaxonRailHelper.rotateCurvingRail(original, context.dispenserFacing(), railAxis);
                if (rotated != null) {
                    return Optional.of(rotated);
                }
            }
            case NORTH, SOUTH, WEST, EAST -> {
                if (dispenserAxis.equals(railAxis)) {
                    // start off with trying to make it ascending
                    @Nullable RailShape toggled = KlaxonRailHelper.tryToggleAscending(context.serverWorld(), original, context.targetPos(), context.dispenserFacing().getOpposite().getAxisDirection());
                    if (toggled != null && !toggled.equals(original)) {
                        return Optional.of(toggled);
                    }
                } else {
                    // then try to correct rail to dispenser direction
                    @Nullable RailShape straightened = KlaxonRailHelper.axisToRailShape(dispenserAxis);
                    if (straightened != null && !straightened.equals(original)) {
                        return Optional.of(straightened);
                    }
                }
            }
        }

        return Optional.empty();
    }
}
