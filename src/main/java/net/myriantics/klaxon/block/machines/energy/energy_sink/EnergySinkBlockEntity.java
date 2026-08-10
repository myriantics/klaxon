package net.myriantics.klaxon.block.machines.energy.energy_sink;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.util.storage.energy.KlaxonEnergyStorageProvider;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class EnergySinkBlockEntity extends BlockEntity implements KlaxonEnergyStorageProvider {

    private static final int MAX_REMAINING_POWERED_TICKS = 8;

    private final EnergyStorage storage;
    protected int remainingPoweredTicks = 0;

    protected EnergySinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.storage = new EnergyStorage() {
            @Override
            public long insert(long maxAmount, TransactionContext transaction) {
                return EnergySinkBlockEntity.this.handleInsertion(maxAmount);
            }

            @Override
            public long extract(long maxAmount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public long getAmount() {
                return 0;
            }

            @Override
            public long getCapacity() {
                return Long.MAX_VALUE;
            }

            @Override
            public boolean supportsExtraction() {
                return false;
            }
        };
    }

    public EnergySinkBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.ENERGY_SINK.value(), pos, state);
    }

    public void serverTick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.isEmittingPower()) {
            if (--this.remainingPoweredTicks < 0) {
                this.stopEmittingPower();
            }
            this.setChanged();
        }
    }

    protected boolean isEmittingPower() {
        return this.getBlockState().getValue(EnergySinkBlock.POWERED);
    }

    protected long handleInsertion(long maxAmount) {
        this.resetRemainingPoweredTicks();
        if (!this.isEmittingPower()) {
            this.startEmittingPower();
        }
        return maxAmount;
    }

    protected void startEmittingPower() {
        if (this.level != null) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(EnergySinkBlock.POWERED, true));
        }
    }

    protected void stopEmittingPower() {
        if (this.level != null) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(EnergySinkBlock.POWERED, false));
        }
    }

    protected void resetRemainingPoweredTicks() {
        this.remainingPoweredTicks = MAX_REMAINING_POWERED_TICKS;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(KlaxonNBTIds.REMAINING_POWERED_TICKS, this.remainingPoweredTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.remainingPoweredTicks = Math.clamp(tag.getInt(KlaxonNBTIds.REMAINING_POWERED_TICKS), 0, MAX_REMAINING_POWERED_TICKS);
    }

    @Override
    public @Nullable EnergyStorage getStorageForSide(@Nullable Direction direction) {
        return this.storage;
    }
}
