package net.myriantics.klaxon.util;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;

public enum RelativeDirection {
    LEFT {
        @Override
        public Direction get(Direction facing, Direction up) {
            return switch (facing) {
                case DOWN -> switch (up) {
                    case DOWN, UP -> throw new AssertionError();
                    case NORTH -> Direction.WEST;
                    case SOUTH -> Direction.EAST;
                    case WEST -> Direction.SOUTH;
                    case EAST -> Direction.NORTH;
                };
                case UP -> switch (up) {
                    case DOWN, UP -> throw new AssertionError();
                    case NORTH -> Direction.EAST;
                    case SOUTH -> Direction.WEST;
                    case WEST -> Direction.NORTH;
                    case EAST -> Direction.SOUTH;
                };
                case NORTH -> switch (up) {
                    case NORTH, SOUTH -> throw new AssertionError();
                    case DOWN -> Direction.EAST;
                    case UP -> Direction.WEST;
                    case WEST -> Direction.DOWN;
                    case EAST -> Direction.UP;
                };
                case SOUTH -> switch (up) {
                    case NORTH, SOUTH -> throw new AssertionError();
                    case DOWN -> Direction.WEST;
                    case UP -> Direction.EAST;
                    case WEST -> Direction.UP;
                    case EAST -> Direction.DOWN;
                };
                case WEST -> switch (up) {
                    case EAST, WEST -> throw new AssertionError();
                    case DOWN -> Direction.NORTH;
                    case UP -> Direction.SOUTH;
                    case NORTH -> Direction.UP;
                    case SOUTH -> Direction.DOWN;
                };
                case EAST -> switch (up) {
                    case EAST, WEST -> throw new AssertionError();
                    case DOWN -> Direction.SOUTH;
                    case UP -> Direction.NORTH;
                    case NORTH -> Direction.DOWN;
                    case SOUTH -> Direction.UP;
                };
            };
        }
    },
    RIGHT {
        @Override
        public Direction get(Direction facing, Direction up) {
            return LEFT.get(facing, up).getOpposite();
        }
    },
    FORWARD {
        @Override
        public Direction get(Direction facing, Direction up) {
            return facing;
        }
    },
    BACKWARD {
        @Override
        public Direction get(Direction facing, Direction up) {
            return facing.getOpposite();
        }
    },
    UP {
        @Override
        public Direction get(Direction facing, Direction up) {
            return up;
        }
    },
    DOWN {
        @Override
        public Direction get(Direction facing, Direction up) {
            return up.getOpposite();
        }
    };

    public abstract Direction get(Direction facing, Direction up);

    public Direction get(FrontAndTop frontAndTop) {
        return this.get(frontAndTop.front(), frontAndTop.top());
    }
}
