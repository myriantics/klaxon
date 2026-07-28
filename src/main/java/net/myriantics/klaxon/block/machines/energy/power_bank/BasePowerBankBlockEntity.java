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

public abstract class BasePowerBankBlockEntity extends BlockEntity implements KlaxonEnergyStorageProvider {

    protected EnergyStorage[] neighborStorages = new EnergyStorage[6];

    protected BasePowerBankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void setNeighboringStorageForSide(Direction side, @Nullable EnergyStorage storage) {
        this.neighborStorages[side.ordinal()] = storage;
    }

    public @Nullable EnergyStorage getNeighboringStorageForSide(Direction side) {
        return this.neighborStorages[side.ordinal()];
    }

    public void serverTick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.canTransfer()) {
            for (@Nullable EnergyStorage storage : this.neighborStorages) {
                if (storage != null) {
                    this.transferInto(storage);
                }
            }
        }
    }

    protected abstract boolean canTransfer();

    protected abstract void transferInto(@Nullable EnergyStorage storage);
}
