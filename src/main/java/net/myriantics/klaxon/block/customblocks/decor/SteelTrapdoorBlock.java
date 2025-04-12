package net.myriantics.klaxon.block.customblocks.decor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SteelTrapdoorBlock extends TrapdoorBlock {
    public SteelTrapdoorBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient()) {
            boolean isRecievingPower = world.isReceivingRedstonePower(pos);

            if (isRecievingPower != state.get(POWERED)) {
                BlockState newState = state.cycle(POWERED);

                // flips on up signal
                if (isRecievingPower) {
                    this.playToggleSound(null, world, pos, !state.get(OPEN));
                    newState = newState.cycle(OPEN);

                    if (state.get(WATERLOGGED)) {
                        world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
                    }
                }

                world.setBlockState(pos, newState);
            }
        }
    }
}
