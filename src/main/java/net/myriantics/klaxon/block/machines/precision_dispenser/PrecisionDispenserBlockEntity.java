package net.myriantics.klaxon.block.machines.precision_dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PrecisionDispenserBlockEntity extends DispenserBlockEntity {
    protected PrecisionDispenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public PrecisionDispenserBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    public float getInaccuracy(float original) {
        return 0;
    }
}
