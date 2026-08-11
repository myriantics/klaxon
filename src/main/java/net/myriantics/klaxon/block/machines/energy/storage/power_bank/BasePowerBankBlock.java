package net.myriantics.klaxon.block.machines.energy.storage.power_bank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public abstract class BasePowerBankBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    protected BasePowerBankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, ENABLED);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!level.isClientSide()) {
            Direction newFacing = state.getValue(FACING);
            if (!state.is(oldState.getBlock()) || newFacing != oldState.getValue(FACING)) {
                if (level.getBlockEntity(pos) instanceof BasePowerBankBlockEntity blockEntity) {
                    blockEntity.setTargetStorage(EnergyStorage.SIDED.find(level, pos.relative(newFacing), newFacing.getOpposite()));
                }
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

        if (!level.isClientSide()) {
            // if energy storage neighbor changed, re-poll energy storage for said neighbor
            @Nullable Direction neighborDirection = Direction.fromDelta(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
            if (neighborDirection != null && neighborDirection == state.getValue(FACING) && level.getBlockEntity(pos) instanceof BasePowerBankBlockEntity blockEntity) {
                blockEntity.setTargetStorage(EnergyStorage.SIDED.find(level, neighborPos, neighborDirection.getOpposite()));
            }

            // update enabled state
            boolean powered = level.hasNeighborSignal(pos);
            if (powered == state.getValue(ENABLED)) {
                level.setBlockAndUpdate(pos, state.setValue(ENABLED, !powered));
            }
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.isSecondaryUseActive() ? context.getClickedFace().getOpposite() : context.getNearestLookingDirection().getOpposite();
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());;
        return this.defaultBlockState().setValue(FACING, facing).setValue(ENABLED, !powered);
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
