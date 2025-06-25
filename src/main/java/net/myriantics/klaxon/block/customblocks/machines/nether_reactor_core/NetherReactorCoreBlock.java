package net.myriantics.klaxon.block.customblocks.machines.nether_reactor_core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetherReactorCoreBlock extends Block {
    public NetherReactorCoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 15;
    }
}
