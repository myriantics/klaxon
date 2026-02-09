package net.myriantics.klaxon.block.machines;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// this exists so you don't get griefed by adjacent components when reading from a nether reactor core with a comparator
// because it swaps between the two blocks
public class CasingBlock extends Block {
    public CasingBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        world.updateComparators(pos, newState.getBlock());
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }
}
