package net.myriantics.klaxon.block.machines.energy.power_bank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.util.storage.KlaxonStorageProvider;
import net.myriantics.klaxon.util.storage.energy.KlaxonEnergyStorageProvider;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.InfiniteEnergyStorage;

public abstract class BasePowerBankBlockEntity extends BlockEntity implements KlaxonEnergyStorageProvider {

    protected @Nullable EnergyStorage targetStorage = null;
    protected EnergyStorage[] neighborStorages = new EnergyStorage[6];

    protected BasePowerBankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(BasePowerBankBlock.FACING);
    }

    public boolean isEnabled() {
        return this.getBlockState().getValue(BasePowerBankBlock.ENABLED);
    }

    public void setTargetStorage(@Nullable EnergyStorage storage) {
        this.targetStorage = storage;
    }

    public void serverTick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.canTransfer()) {
            this.transferInto(this.targetStorage);
        }
    }

    protected boolean canTransfer() {
        return this.isEnabled();
    }

    protected abstract void transferInto(@Nullable EnergyStorage storage);
}
