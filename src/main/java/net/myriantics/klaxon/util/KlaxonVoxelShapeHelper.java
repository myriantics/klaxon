package net.myriantics.klaxon.util;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class KlaxonVoxelShapeHelper {

    public static VoxelShape[] northUpDefaultFrontAndTopRotated(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        final VoxelShape[] shapes = new VoxelShape[FrontAndTop.values().length];
        // blob of trial and error hell
        shapes[FrontAndTop.DOWN_EAST.ordinal()] = Shapes.box(minY, minZ, minX, maxY, maxZ, maxX);
        shapes[FrontAndTop.DOWN_NORTH.ordinal()] = Shapes.box(minX, minZ, 1 - maxY, maxX, maxZ, 1 - minY);
        shapes[FrontAndTop.DOWN_SOUTH.ordinal()] = Shapes.box(minX, minZ, minY, maxX, maxZ, maxY);
        shapes[FrontAndTop.DOWN_WEST.ordinal()] = Shapes.box(1 - maxY, minZ, minX, 1 - minY, maxZ, maxX);
        shapes[FrontAndTop.UP_EAST.ordinal()] = Shapes.box(minY, 1 - maxZ, minX, maxY, 1 - minZ, maxX);
        shapes[FrontAndTop.UP_NORTH.ordinal()] = Shapes.box(minX, 1 - maxZ, 1 - maxY, maxX, 1 - minZ, 1 - minY);
        shapes[FrontAndTop.UP_SOUTH.ordinal()] = Shapes.box(minX, 1 - maxZ, minY, maxX, 1 - minZ, maxY);
        shapes[FrontAndTop.UP_WEST.ordinal()] = Shapes.box(1 - maxY, 1 - maxZ, minX, 1 - minY, 1 - minZ, maxX);
        shapes[FrontAndTop.WEST_UP.ordinal()] = Shapes.box(minZ, minY, minX, maxZ, maxY, maxX);
        shapes[FrontAndTop.EAST_UP.ordinal()] = Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX);
        shapes[FrontAndTop.NORTH_UP.ordinal()] = Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        shapes[FrontAndTop.SOUTH_UP.ordinal()] = Shapes.box(minX, minY, 1 - maxZ, maxX, maxY, 1 - minZ);
        return shapes;
    }

    public static VoxelShape[] arrayUnion(VoxelShape[] arrayA, VoxelShape[] arrayB) {
        if (arrayA.length != arrayB.length) {
            throw new AssertionError("Both voxelshape arrays must be the same length.");
        }
        VoxelShape[] newArray = new VoxelShape[arrayA.length];
        for (int i = 0; i < newArray.length; i++) {
            if (arrayA[i].isEmpty() || arrayB[i].isEmpty()) {
                newArray[i] = Shapes.empty();
            } else {
                newArray[i] = Shapes.or(arrayA[i], arrayB[i]);
            }
        }
        return newArray;
    }
}
