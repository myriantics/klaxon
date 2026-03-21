package net.myriantics.klaxon.block.decor.hallnox_bulb;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface NeighborPlacementListener {

    void onAdjacentPlaceOnSide(Level world, BlockPos clickedPos, BlockState clickedState, BlockPos placedPos, BlockState placedState, BlockPlaceContext context);
}
