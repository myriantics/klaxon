package net.myriantics.klaxon.block.machines.nether_reactor_core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.api.ManualItemApplicationResult;
import net.myriantics.klaxon.api.Wrenchable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NetherReactorCoreBlock extends Block implements Wrenchable, Waterloggable, ManualItemApplicationResult {
    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = Properties.HORIZONTAL_AXIS;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public NetherReactorCoreBlock(Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState()
                .with(HORIZONTAL_AXIS, Direction.Axis.X)
                .with(WATERLOGGED, false)
        );
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 15;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.isOf(this) || !state.get(HORIZONTAL_AXIS).equals(newState.get(HORIZONTAL_AXIS))) {
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState original = super.getPlacementState(ctx);
        return original == null
                ? null
                : original
                        .with(HORIZONTAL_AXIS, ctx.getHorizontalPlayerFacing().getAxis())
                        .with(WATERLOGGED, ctx.getWorld().getBlockState(ctx.getBlockPos()).getFluidState().isOf(Fluids.WATER.getStill()));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HORIZONTAL_AXIS, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public Optional<BlockState> getResultState(World world, BlockState state, BlockPos pos, Direction clickDirection, @Nullable PlayerEntity player) {
        // try using click direction
        if (!clickDirection.getAxis().equals(Direction.Axis.Y)) {
            return Optional.of(state.with(HORIZONTAL_AXIS, clickDirection.getAxis()));
        }

        // try using look direction
        if (player != null) {
            return Optional.of(state.with(HORIZONTAL_AXIS, player.getHorizontalFacing().getAxis()));
        }

        // if both fail return empty, which causes original output state to be used
        return Optional.empty();
    }

    @Override
    public ItemActionResult onWrenched(BlockState targetState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState newState = targetState.cycle(HORIZONTAL_AXIS);

        world.setBlockState(blockPos, newState);
        world.playSound(
                player,
                blockPos,
                this.soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS
        );
        world.emitGameEvent(
                GameEvent.BLOCK_CHANGE,
                blockPos,
                GameEvent.Emitter.of(player, newState)
        );

        return ItemActionResult.SUCCESS;
    }

    @Override
    public ItemActionResult onDispenserWrenched(BlockState targetState, BlockPos targetPos, ItemStack stack, ServerWorld serverWorld, Direction facing, BlockPointer pointer) {
        BlockState newState = targetState.cycle(HORIZONTAL_AXIS);

        serverWorld.setBlockState(targetPos, newState);
        serverWorld.playSound(
                null,
                targetPos,
                this.soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS
        );
        serverWorld.emitGameEvent(
                GameEvent.BLOCK_CHANGE,
                targetPos,
                GameEvent.Emitter.of(newState)
        );

        return ItemActionResult.SUCCESS;
    }
}
