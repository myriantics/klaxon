package net.myriantics.klaxon.block.machines.energy.power_bank.creative;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.energy.power_bank.BasePowerBankBlockEntity;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.InfiniteEnergyStorage;

public class CreativePowerBankBlockEntity extends BasePowerBankBlockEntity {
    protected CreativePowerBankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public CreativePowerBankBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.CREATIVE_POWER_BANK.value(), pos, state);
    }

    @Override
    public @Nullable EnergyStorage getStorageForSide(@Nullable Direction direction) {
        return direction == null || direction == this.getFacing() ? InfiniteEnergyStorage.INSTANCE : null;
    }

    @Override
    protected void transferInto(@Nullable EnergyStorage storage) {
        if (storage != null && storage.supportsInsertion()) {
            try (Transaction tx = Transaction.openOuter()) {
                storage.insert(storage.getCapacity(), tx);
            }
        }
    }
}
