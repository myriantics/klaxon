package net.myriantics.klaxon.block.machines.energy.power_bank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.myriantics.klaxon.block.machines.energy.contact_charger.BaseContactChargerBlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public abstract class BasePowerBankBlock extends BaseEntityBlock {
    protected BasePowerBankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.getBlockEntity(pos) instanceof BasePowerBankBlockEntity blockEntity) {
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                blockEntity.setNeighboringStorageForSide(direction, EnergyStorage.SIDED.find(level, pos.relative(direction), direction.getOpposite()));
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        @Nullable Direction neighborDirection = Direction.fromDelta(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getX(), neighborPos.getZ() - pos.getZ());
        if (neighborDirection != null && level.getBlockEntity(pos) instanceof BasePowerBankBlockEntity blockEntity) {
            blockEntity.setNeighboringStorageForSide(neighborDirection, EnergyStorage.SIDED.find(level, neighborPos, neighborDirection.getOpposite()));
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (level1, blockPos, blockState, blockEntity) -> {
            if (blockEntity instanceof BasePowerBankBlockEntity powerBankBlockEntity) {
                powerBankBlockEntity.serverTick(level1, blockPos, blockState);
            }
        };
    }
}
