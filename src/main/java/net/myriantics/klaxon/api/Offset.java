package net.myriantics.klaxon.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.myriantics.klaxon.KlaxonCommon;

public enum Offset {
    UP(Direction.UP),
    UP_NORTH(Direction.UP, Direction.NORTH),
    UP_EAST(Direction.UP, Direction.EAST),
    UP_SOUTH(Direction.UP, Direction.SOUTH),
    UP_WEST(Direction.UP, Direction.WEST),
    UP_NORTH_EAST(Direction.UP, Direction.NORTH, Direction.EAST),
    UP_SOUTH_EAST(Direction.UP, Direction.SOUTH, Direction.EAST),
    UP_SOUTH_WEST(Direction.UP, Direction.SOUTH, Direction.WEST),
    UP_NORTH_WEST(Direction.UP, Direction.NORTH, Direction.WEST),
    DOWN(Direction.DOWN),
    DOWN_NORTH(Direction.DOWN, Direction.NORTH),
    DOWN_EAST(Direction.DOWN, Direction.EAST),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
    DOWN_WEST(Direction.DOWN, Direction.WEST),
    DOWN_NORTH_EAST(Direction.DOWN, Direction.NORTH, Direction.EAST),
    DOWN_SOUTH_EAST(Direction.DOWN, Direction.SOUTH, Direction.EAST),
    DOWN_SOUTH_WEST(Direction.DOWN, Direction.SOUTH, Direction.WEST),
    DOWN_NORTH_WEST(Direction.DOWN, Direction.NORTH, Direction.WEST),
    NORTH(Direction.NORTH),
    NORTH_EAST(Direction.NORTH, Direction.EAST),
    EAST(Direction.EAST),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST),
    SOUTH(Direction.SOUTH),
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),
    WEST(Direction.SOUTH, Direction.WEST),
    NORTH_WEST(Direction.NORTH, Direction.WEST);

    private final Vec3i offsetVector;
    private final Direction[] directions;

    public static final Offset[] ADJACENT = {
            UP,
            DOWN,
            NORTH,
            SOUTH,
            EAST,
            WEST
    };

    public static final Offset[] ADJACENT_AND_DIAGONAL = {
            UP,
            UP_NORTH,
            UP_EAST,
            UP_SOUTH,
            UP_WEST,
            DOWN,
            DOWN_NORTH,
            DOWN_EAST,
            DOWN_SOUTH,
            DOWN_WEST,
            NORTH,
            NORTH_EAST,
            NORTH_WEST,
            SOUTH,
            SOUTH_EAST,
            SOUTH_WEST,
            EAST,
            WEST
    };

    public static final Offset[] ALL = values();

    Offset(Direction... directions) {
        this.directions = directions;
        Vec3i offsetVector = new Vec3i(0, 0, 0);
        for (Direction direction : directions) {
            offsetVector = offsetVector.add(direction.getVector());
        }
        this.offsetVector = offsetVector;
    }

    public BlockPos offset(BlockPos pos) {
        return pos.add(offsetVector);
    }

    public Vec3i offset(Vec3i vec3i) {
        return vec3i.add(offsetVector);
    }

    public Vec3i getOffsetVector() {
        return offsetVector;
    }

    static {
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (Offset offset : values()) {
            pos.move(offset.offsetVector);
        }
    }
}
