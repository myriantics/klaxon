package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface PipeMatrix {
    boolean sideHasExposedPipes(BlockState state, Direction direction);
}
