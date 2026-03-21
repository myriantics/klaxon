package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.KlaxonRailHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CurvingRailShapeBlockStateWrenchBehavior extends BlockStateWrenchBehavior<RailShape> {
    public CurvingRailShapeBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.RAIL_SHAPE, id);
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
