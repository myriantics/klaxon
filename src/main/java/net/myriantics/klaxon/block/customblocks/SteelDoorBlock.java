package net.myriantics.klaxon.block.customblocks;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.mixin.DoorBlockInvoker;

public class SteelDoorBlock extends DoorBlock {
    public SteelDoorBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient()) {
            boolean isRecievingPower = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));

            if (isRecievingPower != state.get(POWERED)) {
                BlockState newState = state.cycle(POWERED);

                // flips on up signal
                if (!this.getDefaultState().isOf(sourceBlock) && isRecievingPower) {
                    // dude these random ass private methods have me tweaking
                    ((DoorBlockInvoker) state.getBlock()).klaxon$playOpenCloseSound(null, world, pos, !state.get(OPEN));
                    newState = newState.cycle(OPEN);
                }

                world.setBlockState(pos, newState);
            }
        }
    }
}
