package net.myriantics.klaxon.util;

import net.minecraft.util.math.Direction;

public abstract class BlockDirectionHelper {
    public static Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public static Direction[] ALL = Direction.values();

    public static Direction getUp(Direction blockDirection) {
        return Direction.UP;
    }
    public static Direction getDown(Direction blockDirection) {
        return Direction.DOWN;
    }
    public static Direction getLeft(Direction blockDirection) {
        return blockDirection.rotateClockwise(Direction.Axis.Y);
    }
    public static Direction getRight(Direction blockDirection) {
        return blockDirection.rotateCounterclockwise(Direction.Axis.Y);
    }
    public static Direction getFront(Direction blockDirection) {
        return blockDirection;
    }
    public static Direction getBack(Direction blockDirection) {
        return blockDirection.rotateClockwise(Direction.Axis.Y).rotateClockwise(Direction.Axis.Y);
    }
}
