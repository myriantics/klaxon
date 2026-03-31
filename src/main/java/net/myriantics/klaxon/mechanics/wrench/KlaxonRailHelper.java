package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.myriantics.klaxon.util.RelativeDirection;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonRailHelper {

    public static boolean canAscend(Level level, RailShape railShape, BlockPos railPos, Direction ascensionDirection) {
        Direction.Axis railAxis = railShapeToAxis(railShape);
        return ascensionDirection.getAxis().equals(railAxis) && !railShape.isAscending() && BaseRailBlock.canSupportRigidBlock(level, railPos.relative(ascensionDirection));
    }

    public static RailShape getLowered(RailShape railShape) throws IllegalArgumentException {
        return switch (railShape) {
            case ASCENDING_EAST, ASCENDING_WEST -> RailShape.EAST_WEST;
            case ASCENDING_NORTH, ASCENDING_SOUTH -> RailShape.NORTH_SOUTH;
            default -> throw new IllegalArgumentException("RailShape argument must be ascending - was [" + railShape + "]!");
        };
    }

    public static RailShape getRaised(RailShape railShape, Direction.AxisDirection direction) throws IllegalArgumentException {
        return switch (railShape) {
            case NORTH_SOUTH -> switch (direction) {
                case POSITIVE -> RailShape.ASCENDING_SOUTH;
                case NEGATIVE -> RailShape.ASCENDING_NORTH;
            };
            case EAST_WEST -> switch (direction) {
                case POSITIVE -> RailShape.ASCENDING_EAST;
                case NEGATIVE -> RailShape.ASCENDING_WEST;
            };
            default -> throw new IllegalArgumentException("RailShape argument must not be curved or ascending - was [" + railShape + "]");
        };
    }

    public static @Nullable RailShape axisToRailShape(Direction.Axis axis) {
        return switch (axis) {
            case X -> RailShape.EAST_WEST;
            case Z -> RailShape.NORTH_SOUTH;
            case Y -> null;
        };
    }

    public static @Nullable Direction.Axis railShapeToAxis(RailShape railShape) {
        return switch (railShape) {
            case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> Direction.Axis.Z;
            case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST -> Direction.Axis.X;
            default -> null;
        };
    }

    public static @Nullable RailShape tryCurvingRail(RailShape railShape, Direction.AxisDirection facingAxisDir, RelativeDirection relativeDirection) {
        return switch (railShape) {
            case NORTH_SOUTH -> switch (relativeDirection) {
                case LEFT -> switch (facingAxisDir) {
                    case POSITIVE -> RailShape.NORTH_WEST;
                    case NEGATIVE -> RailShape.SOUTH_EAST;
                };
                case RIGHT -> switch (facingAxisDir) {
                    case POSITIVE -> RailShape.NORTH_EAST;
                    case NEGATIVE -> RailShape.SOUTH_WEST;
                };
                default -> null;
            };
            case EAST_WEST -> switch (relativeDirection) {
                case LEFT -> switch (facingAxisDir) {
                    case POSITIVE -> RailShape.NORTH_WEST;
                    case NEGATIVE -> RailShape.SOUTH_EAST;
                };
                case RIGHT -> switch (facingAxisDir) {
                    case POSITIVE -> RailShape.SOUTH_WEST;
                    case NEGATIVE -> RailShape.NORTH_EAST;
                };
                default -> null;
            };
            default -> null;
        };
    }

    public static RailShape rotateClockwise(RailShape railShape) {
        return switch (railShape) {
            case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> RailShape.EAST_WEST;
            case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST -> RailShape.NORTH_SOUTH;
            case SOUTH_EAST -> RailShape.SOUTH_WEST;
            case SOUTH_WEST -> RailShape.NORTH_WEST;
            case NORTH_WEST -> RailShape.NORTH_EAST;
            case NORTH_EAST -> RailShape.SOUTH_EAST;
        };
    }

    public static RailShape rotateCounterClockwise(RailShape railShape) {
        return switch (railShape) {
            case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> RailShape.EAST_WEST;
            case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST -> RailShape.NORTH_SOUTH;
            case SOUTH_EAST -> RailShape.NORTH_EAST;
            case SOUTH_WEST -> RailShape.SOUTH_EAST;
            case NORTH_WEST -> RailShape.SOUTH_WEST;
            case NORTH_EAST -> RailShape.NORTH_WEST;
        };
    }
}
