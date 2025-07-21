package net.myriantics.klaxon.block.customblocks.decor.custom_hanging_sign;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class CustomWallHangingSignBlock extends WallHangingSignBlock {
    public CustomWallHangingSignBlock(WoodType woodType, Settings settings) {
        super(woodType, settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CustomHangingSignBlockEntity(pos, state);
    }
}
