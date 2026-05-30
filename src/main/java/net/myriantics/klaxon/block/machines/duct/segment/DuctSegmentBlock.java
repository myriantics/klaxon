package net.myriantics.klaxon.block.machines.duct.segment;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.myriantics.klaxon.mechanics.logistics.itemduct.IDuctNodeBlock;
import org.jetbrains.annotations.Nullable;

public class DuctSegmentBlock extends PipeBlock implements EntityBlock, IDuctNodeBlock {

    private static final float APOTHEM = 6f/16;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DuctSegmentBlock(Properties properties) {
        super(APOTHEM, properties);

        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return simpleCodec(DuctSegmentBlock::new);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (oldState.is(this)) {
            DuctSegmentBlockEntity blockEntity = null;
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction);
                boolean newDirectionValue = state.getValue(property);
                if (newDirectionValue != oldState.getValue(property)) {
                    if (blockEntity == null && level.getBlockEntity(pos) instanceof DuctSegmentBlockEntity be) {
                        blockEntity = be;
                    } else {
                        break;
                    }
                    blockEntity.updateDirections(direction, null, newDirectionValue);
                }
            }
        } else {
            if (level.getBlockEntity(pos) instanceof DuctSegmentBlockEntity blockEntity) {
                for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                    BlockPos neighborPos = pos.relative(direction);
                    BlockState neighborState = level.getBlockState(neighborPos);
                    boolean isDirectionExtended = state.getValue(PROPERTY_BY_DIRECTION.get(direction));
                    blockEntity.updateDirections(direction, null, isDirectionExtended);
                    if (isDirectionExtended) {
                        Direction opposite = direction.getOpposite();
                        // update state
                        if (neighborState.getBlock() instanceof IDuctNodeBlock nodeBlock && !nodeBlock.isConnectionOpen(neighborState, opposite)) {
                            level.setBlockAndUpdate(neighborPos, nodeBlock.setConnectionForFace(neighborState, opposite, true));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide()) {
            Direction updatedDirection = Direction.fromDelta(
                    neighborPos.getX() - pos.getX(),
                    neighborPos.getY() - pos.getY(),
                    neighborPos.getZ() - pos.getZ()
            );
            if (updatedDirection != null) {
                BooleanProperty dirProperty = PROPERTY_BY_DIRECTION.get(updatedDirection);
                boolean extended = state.getValue(dirProperty);
                Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, neighborPos, updatedDirection.getOpposite());
                if (storage != null) {
                    level.setBlockAndUpdate(pos, state.setValue(dirProperty, true));
                    extended = true;
                }
                if (level.getBlockEntity(pos) instanceof DuctSegmentBlockEntity blockEntity) {
                    blockEntity.updateDirections(updatedDirection, storage, extended);
                }
            }
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        Level level = context.getLevel();
        boolean sneaking = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        if (sneaking) {
            return state.setValue(PROPERTY_BY_DIRECTION.get(context.getClickedFace().getOpposite()), true);
        } else {
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                BlockPos neighborPos = pos.relative(direction);
                BlockState neighborState = level.getBlockState(neighborPos);
                Direction selectedNeighborFace = direction.getOpposite();
                boolean shouldExtend = neighborState.getBlock() instanceof IDuctNodeBlock nodeBlock && nodeBlock.canConnectionOpen(neighborState, selectedNeighborFace);
                if (!shouldExtend) {
                    Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, neighborPos, neighborState, null, direction.getOpposite());
                    shouldExtend = storage != null;
                }
                if (shouldExtend) {
                    state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
                }
            }
            return state;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        DuctSegmentBlockEntity blockEntity = new DuctSegmentBlockEntity(pos, state);
        for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
            blockEntity.updateDirections(direction, null, state.getValue(PROPERTY_BY_DIRECTION.get(direction)));
        }
        return blockEntity;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return EntityBlock.super.getTicker(level, state, blockEntityType);
    }

    @Override
    public boolean canConnectionOpen(BlockState state, Direction face) {
        return true;
    }

    @Override
    public boolean isConnectionOpen(BlockState state, Direction face) {
        return state.getValue(PROPERTY_BY_DIRECTION.get(face));
    }

    @Override
    public BlockState setConnectionForFace(BlockState original, Direction face, boolean connected) {
        return original.setValue(PROPERTY_BY_DIRECTION.get(face), connected);
    }
}
