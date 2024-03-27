package net.myriantics.klaxon.block.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.block.ModBlockEntities;

public class CrudeExtrapolatorBlockEntity extends BlockEntity {

    public CrudeExtrapolatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUDE_EXTRAPOLATOR_BLOCK_ENTITY, pos, state);
    }
}
