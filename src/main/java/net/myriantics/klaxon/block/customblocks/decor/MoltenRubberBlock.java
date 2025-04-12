package net.myriantics.klaxon.block.customblocks.decor;

import net.minecraft.block.BlockState;
import net.minecraft.block.Degradable;
import net.minecraft.block.MagmaBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.NeighborUpdater;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonFluidTags;

import java.util.Optional;

public class MoltenRubberBlock extends MagmaBlock {
    public MoltenRubberBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient()) return;

        for (Direction dir : NeighborUpdater.UPDATE_ORDER) {
            if (testForColdness(world, pos.offset(dir))) world.setBlockState(pos, KlaxonBlocks.RUBBER_BLOCK.getDefaultState());
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient() && testForColdness(world, neighborPos)) {
            return KlaxonBlocks.RUBBER_BLOCK.getDefaultState();
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean testForColdness(WorldAccess world, BlockPos pos) {
        return world.getBlockState(pos).isIn(KlaxonBlockTags.COLD_BLOCKS) || world.getFluidState(pos).isIn(KlaxonFluidTags.COLD_FLUIDS);
    }
}
