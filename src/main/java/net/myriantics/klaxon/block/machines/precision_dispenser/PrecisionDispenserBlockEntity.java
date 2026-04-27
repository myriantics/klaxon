package net.myriantics.klaxon.block.machines.precision_dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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

    @Override
    public int getRandomSlot(RandomSource random) {
        this.unpackLootTable(null);

        // select in sequence
        for (int i = 0; i < this.getItems().size(); i++) {
            if (!this.getItem(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public float getInaccuracy(float original) {
        return 0;
    }
}
