package net.myriantics.klaxon.block.machines.nether_reactor_core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NetherReactorCoreBlock extends Block implements SimpleWaterloggedBlock, WorldItemApplicationResult {
    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public NetherReactorCoreBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(defaultBlockState()
                .setValue(HORIZONTAL_AXIS, Direction.Axis.X)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return 15;
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.is(this) || !state.getValue(HORIZONTAL_AXIS).equals(newState.getValue(HORIZONTAL_AXIS))) {
            world.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState original = super.getStateForPlacement(ctx);
        return original == null
                ? null
                : original
                        .setValue(HORIZONTAL_AXIS, ctx.getHorizontalDirection().getAxis())
                        .setValue(WATERLOGGED, ctx.getLevel().getBlockState(ctx.getClickedPos()).getFluidState().is(Fluids.WATER.getSource()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_AXIS, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.getValue(HORIZONTAL_AXIS)) {
                    case Z:
                        return state.setValue(HORIZONTAL_AXIS, Direction.Axis.X);
                    case X:
                        return state.setValue(HORIZONTAL_AXIS, Direction.Axis.Z);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    public Optional<BlockState> getResultState(Level world, BlockState state, BlockPos pos, Direction clickDirection, @Nullable Player player) {
        // try using click direction
        if (!clickDirection.getAxis().equals(Direction.Axis.Y)) {
            return Optional.of(state.setValue(HORIZONTAL_AXIS, clickDirection.getAxis()));
        }

        // try using look direction
        if (player != null) {
            return Optional.of(state.setValue(HORIZONTAL_AXIS, player.getDirection().getAxis()));
        }

        // if both fail return empty, which causes original output state to be used
        return Optional.empty();
    }
}
