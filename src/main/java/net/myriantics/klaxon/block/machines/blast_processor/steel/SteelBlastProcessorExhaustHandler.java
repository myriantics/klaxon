package net.myriantics.klaxon.block.machines.blast_processor.steel;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface SteelBlastProcessorExhaustHandler {
    boolean klaxon$handleExhaust(Level level, BlockPos pos, BlockState state);
}
