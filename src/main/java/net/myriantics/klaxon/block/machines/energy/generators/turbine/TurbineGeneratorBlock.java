package net.myriantics.klaxon.block.machines.energy.generators.turbine;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.myriantics.klaxon.block.machines.energy.storage.power_bank.BasePowerBankBlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.BiConsumer;

public class TurbineGeneratorBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final MapCodec<TurbineGeneratorBlock> CODEC = simpleCodec(TurbineGeneratorBlock::new);

    public TurbineGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide()
                ? null
                : (level1, blockPos, blockState, blockEntity) -> {
            if (blockEntity instanceof TurbineGeneratorBlockEntity turbineGeneratorBlockEntity) {
                turbineGeneratorBlockEntity.serverTick((ServerLevel) level1, blockPos, blockState);
            }
        };
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TurbineGeneratorBlockEntity(pos, state);
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
        if (explosion.canTriggerBlocks() && level.getBlockEntity(pos) instanceof TurbineGeneratorBlockEntity blockEntity) {
            blockEntity.boost();
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!level.isClientSide()) {
            Direction newFacing = state.getValue(FACING);
            if (!state.is(oldState.getBlock()) || newFacing != oldState.getValue(FACING)) {
                if (level.getBlockEntity(pos) instanceof TurbineGeneratorBlockEntity blockEntity) {
                    blockEntity.setTargetStorage(EnergyStorage.SIDED.find(level, pos.relative(newFacing.getOpposite()), newFacing));
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
            if (neighborDirection != null && neighborDirection == state.getValue(FACING).getOpposite() && level.getBlockEntity(pos) instanceof TurbineGeneratorBlockEntity blockEntity) {
                blockEntity.setTargetStorage(EnergyStorage.SIDED.find(level, neighborPos, neighborDirection.getOpposite()));
            }
        }
    }

    public static boolean conductsTurbineThrust(Level level, BlockPos pos, BlockState state) {
        return true;
    }
}
