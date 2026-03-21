package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface PipeMatrix {
    boolean sideHasExposedPipes(BlockState state, Direction direction);
}
