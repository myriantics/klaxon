package net.myriantics.klaxon.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

// this exists so you don't get griefed by adjacent components when reading from a nether reactor core with a comparator
// because it swaps between the two blocks
public class CasingBlock extends Block {
    public CasingBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        world.updateNeighbourForOutputSignal(pos, newState.getBlock());
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
}
