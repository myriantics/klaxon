package net.myriantics.klaxon.block.machines.duct.driver.aio;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.myriantics.klaxon.mechanics.logistics.itemduct.IDuctNodeBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class AIODuctDriverBlock extends BaseEntityBlock implements IDuctNodeBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty POWER_SOCKET_FACING = KlaxonBlockStateProperties.POWER_SOCKET_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AIODuctDriverBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(POWERED, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(AIODuctDriverBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED, POWERED);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && (!oldState.is(this) || oldState.getValue(FACING) != state.getValue(FACING)) && level.getBlockEntity(pos) instanceof AIODuctDriverBlockEntity blockEntity) {
            blockEntity.updateCaches();
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide()) {
            if (neighborPos.equals(pos.relative(state.getValue(FACING))) && level.getBlockEntity(pos) instanceof AIODuctDriverBlockEntity be) {
                be.updateCaches();
            }
            level.setBlockAndUpdate(pos, state.setValue(POWERED, level.hasNeighborSignal(pos)));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, KlaxonBlockEntityTypes.AIO_DUCT_DRIVER.value(), (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AIODuctDriverBlockEntity(pos, state);
    }

    @Override
    public boolean canConnectionOpen(BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    }

    @Override
    public boolean isConnectionOpen(BlockState state, Direction face) {
        return state.getValue(FACING) == face.getOpposite();
    }

    @Override
    public BlockState setConnectionForFace(BlockState original, Direction face, boolean connected) {
        return original;
    }
}
