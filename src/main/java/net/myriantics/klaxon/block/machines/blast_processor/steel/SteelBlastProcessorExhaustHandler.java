package net.myriantics.klaxon.block.machines.blast_processor.steel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface SteelBlastProcessorExhaustHandler {
    boolean klaxon$handleExhaust(ServerLevel level, BlockPos pos, BlockState state);
}
