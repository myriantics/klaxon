package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.block.machines.BaseKlaxonDoubleTallMachineBlock;
import org.jetbrains.annotations.Nullable;

public abstract class BaseIndustrialShredderBlock extends BaseKlaxonDoubleTallMachineBlock implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BaseIndustrialShredderBlock(Properties properties, Part part) {
        super(properties, part);

        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected @Nullable BlockState getCounterpartStateForState(BlockState state) {
        if (!state.is(this)) {
            return null;
        }

        return this.getCounterpartBlock().value().defaultBlockState().setValue(FACING, state.getValue(FACING));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

        if (level.getBlockEntity(pos) instanceof BaseIndustrialShredderBlockEntity be) {
            if (!level.isClientSide()) {
                player.openMenu(be);
            }
            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == this.part.counterpartOffsetDirection && neighborState.is(this.getCounterpartBlock().value())) {
            BlockState counterpartState = ((BaseIndustrialShredderBlock) this.getCounterpartBlock().value()).getCounterpartStateForState(neighborState);
            if (counterpartState != null) {
                return counterpartState;
            }
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();

        BlockState state = this.defaultBlockState().setValue(FACING, facing.getOpposite());

        BlockPos placementPos = context.getClickedPos();
        BlockPos counterpartPos = placementPos.relative(this.part.counterpartOffsetDirection);
        @Nullable BlockState counterpartState = this.getCounterpartStateForState(state);

        if (counterpartState == null || !this.canPlaceCounterpartAt(context.getPlayer(), context.getLevel(), counterpartPos, counterpartState)) {
            return null;
        }

        return state;
    }
}
