package net.myriantics.klaxon.block.customblocks.decor.custom_hanging_sign;

import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class CustomHangingSignBlock extends HangingSignBlock {
    public CustomHangingSignBlock(WoodType woodType, Settings settings) {
        super(woodType, settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CustomHangingSignBlockEntity(pos, state);
    }
}
