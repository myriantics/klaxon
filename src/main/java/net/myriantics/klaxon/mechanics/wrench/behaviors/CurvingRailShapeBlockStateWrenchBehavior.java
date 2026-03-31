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
    protected Optional<RailShape> applyManual(RailShape original, ManualWrenchInteractionContext context) {
        Direction playerFacing = context.player().getDirection();
        BlockPos railPos = context.hitResult().getBlockPos();
        Position hitPos = context.hitResult().getLocation();

        Vec3 railCenterPos = railPos.getCenter();

        @Nullable Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(original);
        Direction.Axis lookAxis = playerFacing.getAxis();

        // try to toggle ascension / descension first
        if (original.isAscending() || lookAxis.equals(railAxis)) {
            @Nullable RailShape newShape = KlaxonRailHelper.tryToggleAscending(context.world(), original, railPos, playerFacing.getAxisDirection());
            if (newShape != null && !newShape.equals(original)) {
                return Optional.of(newShape);
            }
        }

        // if the rail is curved, try to straighten it out
        @Nullable RailShape newShape = KlaxonRailHelper.axisToRailShape(lookAxis);
        if (newShape != null && !newShape.equals(original)) {
            return Optional.of(newShape);
        }

        // only try to rotate straight rails when no other operations have been performed
        if (railAxis != null) {
            // we're already going to rotate it, so broaden search to either side of the rail
            Direction clickDirection =
                    Direction.getNearest(railAxis.equals(Direction.Axis.X)
                            ? new Vec3(0, 0, hitPos.z() - railCenterPos.z())
                            : new Vec3(hitPos.x() - railCenterPos.x(), 0, 0)
                    );

            // if player clicks on the axis perpendicular to looking axis, rotate rail.
            switch (clickDirection) {
                case NORTH -> {
                    return Optional.of(playerFacing.equals(Direction.WEST) ? RailShape.NORTH_EAST : RailShape.NORTH_WEST);
                }
                case SOUTH -> {
                    return Optional.of(playerFacing.equals(Direction.WEST) ? RailShape.SOUTH_EAST : RailShape.SOUTH_WEST);
                }
                case WEST -> {
                    return Optional.of(playerFacing.equals(Direction.NORTH) ? RailShape.SOUTH_WEST : RailShape.NORTH_WEST);
                }
                case EAST -> {
                    return Optional.of(playerFacing.equals(Direction.NORTH) ? RailShape.SOUTH_EAST : RailShape.NORTH_EAST);
                }
            }
        }

        return Optional.empty();
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
