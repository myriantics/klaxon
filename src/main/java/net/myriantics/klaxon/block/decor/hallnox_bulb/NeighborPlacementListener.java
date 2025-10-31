package net.myriantics.klaxon.block.decor.hallnox_bulb;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface NeighborPlacementListener {

    void onAdjacentPlaceOnSide(World world, BlockPos clickedPos, BlockState clickedState, BlockPos placedPos, BlockState placedState, ItemPlacementContext context);
}
