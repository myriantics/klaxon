package net.myriantics.klaxon.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseKlaxonMachineBlockEntity extends BlockEntity {



    protected BaseKlaxonMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    boolean initialized = false;

    public final void baseTick() {
        if (!this.initialized) {
            this.initialize();
        }
    }

    protected void initialize() {
    }
}
