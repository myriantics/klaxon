package net.myriantics.klaxon.util;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class KlaxonRailHelper {

    public static boolean isCurved(RailShape railShape) {
        switch (railShape) {
            case SOUTH_EAST, NORTH_EAST, NORTH_WEST, SOUTH_WEST -> {
                return true;
            }
        }

        // if we didnt hit any curved ones, it's not curved
        return false;
    }

    public static boolean isStraight(RailShape railShape, boolean allowAscending) {
        switch (railShape) {
            case NORTH_SOUTH, EAST_WEST -> {
                return true;
            }
            case ASCENDING_NORTH, ASCENDING_SOUTH, ASCENDING_EAST, ASCENDING_WEST -> {
                return allowAscending;
            }
        }

        // if we get here, it's curving
        return false;
    }

    public static @Nullable RailShape tryToggleAscending(World world, RailShape railShape, BlockPos railPos, Direction.AxisDirection ascensionDirection) {
        Direction.Axis railAxis = railShapeToAxis(railShape);
        if (railAxis == null) return null;
        // check to see if block can support ascending rails before doing it - won't stop you from correcting a wrongly ascending one, though
        if (railShape.isAscending() || AbstractRailBlock.hasTopRim(world, railPos.offset(Direction.from(railAxis, ascensionDirection)))) {
            return toggleAscent(railShape, ascensionDirection);
        }

        return null;
    }

    public static Direction.Axis flipHorizontalAxis(Direction.Axis axis) {
        switch (axis) {
            case X -> {
                return Direction.Axis.Z;
            }
            case Z -> {
                return Direction.Axis.X;
            }
        }

        return null;
    }

    private static @Nullable RailShape toggleAscent(RailShape railShape, Direction.AxisDirection direction) {
        switch (railShape) {
            case NORTH_SOUTH -> {
                return direction.equals(Direction.AxisDirection.POSITIVE) ? RailShape.ASCENDING_SOUTH : RailShape.ASCENDING_NORTH;
            }
            case EAST_WEST -> {
                return direction.equals(Direction.AxisDirection.POSITIVE) ? RailShape.ASCENDING_EAST : RailShape.ASCENDING_WEST;
            }
            case ASCENDING_EAST, ASCENDING_WEST -> {
                return RailShape.EAST_WEST;
            }
            case ASCENDING_NORTH, ASCENDING_SOUTH -> {
                return RailShape.NORTH_SOUTH;
            }
        }

        return null;
    }

    public static @Nullable RailShape axisToRailShape(Direction.Axis axis) {
        switch (axis) {
            case X -> {
                return RailShape.EAST_WEST;
            }
            case Z -> {
                return RailShape.NORTH_SOUTH;
            }
        }

        return null;
    }

    public static @Nullable Direction.Axis railShapeToAxis(RailShape railShape) {
        switch (railShape) {
            case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> {
                return Direction.Axis.Z;
            }
            case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST -> {
                return Direction.Axis.X;
            }
        }

        return null;
    }

    public static @Nullable RailShape rotateCurvingRail(RailShape railShape, Direction dispenserFacing, Direction.Axis railAxis) {
        boolean inverted = dispenserFacing.equals(Direction.DOWN);
        RailShape rotated = null;

        switch (railShape) {
            case NORTH_SOUTH -> {
                rotated = RailShape.EAST_WEST;
            }
            case EAST_WEST -> {
                rotated = RailShape.NORTH_SOUTH;
            }
            case SOUTH_EAST -> {
                rotated = inverted ? RailShape.NORTH_EAST : RailShape.SOUTH_WEST;
            }
            case SOUTH_WEST -> {
                rotated = inverted ? RailShape.SOUTH_EAST : RailShape.NORTH_WEST;
            }
            case NORTH_WEST -> {
                rotated = inverted ? RailShape.SOUTH_WEST : RailShape.NORTH_EAST;
            }
            case NORTH_EAST -> {
                rotated = inverted ? RailShape.NORTH_WEST : RailShape.SOUTH_EAST;
            }
        }
        return rotated;
    }
}
