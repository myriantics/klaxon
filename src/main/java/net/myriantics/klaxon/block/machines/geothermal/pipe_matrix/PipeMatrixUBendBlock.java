package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.api.NeighborPlacementListener;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PipeMatrixUBendBlock extends Block implements Wrenchable, PipeMatrix, NeighborPlacementListener {
    // Tracks the axis the pipes turns around.
    // This refers to the axis as if the facing direction was the Y axis.
    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = Properties.HORIZONTAL_AXIS;
    // Tracks the direction the pipe interface point is in.
    public static final DirectionProperty FACING = Properties.FACING;
    // Tracks whether this pipe matrix loop is part of a valid structure or not.
    public static final BooleanProperty FORMED = KlaxonBlockStateProperties.FORMED;

    private PipeMatrixSegmentBlock segmentBlock;

    public PipeMatrixUBendBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.getDefaultState()
                .with(HORIZONTAL_AXIS, Direction.Axis.X)
                .with(FACING, Direction.UP)
                .with(FORMED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HORIZONTAL_AXIS, FACING, FORMED);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return neighborState instanceof PipeMatrix && neighborState.get(FORMED) ? state : state.with(FORMED, false);
    }

    public static Optional<BlockState> withAxisIfPossible(BlockState state, Direction.Axis clickedAxis, Direction.Axis facingAxis) {
        Optional<BlockState> result = withAxisIfPossible(state, clickedAxis);
        if (result.isEmpty()) {
            result = withAxisIfPossible(state, facingAxis);
        }
        return result;
    }

    public static Optional<BlockState> withAxisIfPossible(BlockState state, Direction.Axis axis) {
        Optional<BlockState> result = Optional.empty();

        switch (state.get(FACING).getAxis()) {
            case X -> {
                switch (axis) {
                    case Y -> result = Optional.of(state.with(HORIZONTAL_AXIS, Direction.Axis.X));
                    case Z -> result = Optional.of(state.with(HORIZONTAL_AXIS, Direction.Axis.Z));
                }
            }
            case Y -> {
                switch (axis) {
                    case X -> result = Optional.of(state.with(HORIZONTAL_AXIS, Direction.Axis.X));
                    case Z -> result = Optional.of(state.with(HORIZONTAL_AXIS, Direction.Axis.Z));
                }
            }
            case Z -> {
                switch (axis) {
                    case X -> result = Optional.of(state.with(HORIZONTAL_AXIS, Direction.Axis.X));
                    case Y -> result = Optional.of(state.with(HORIZONTAL_AXIS, Direction.Axis.Z));
                }
            }
        }

        return result;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction clickedDirection = ctx.getSide();
        Direction facingDirection = ctx.getPlayerLookDirection();

        // this method is only called from the pipe matrix block, and we verify that the clicked block is safe there.
        BlockState newState = this.getDefaultState().with(FACING, clickedDirection);

        return withAxisIfPossible(
                newState,
                clickedDirection.getAxis(),
                facingDirection.getAxis()
        ).orElse(newState);
    }

    @Override
    public boolean sideHasExposedPipes(BlockState state, Direction direction) {
        return direction.equals(state.get(FACING));
    }

    @Override
    public Item asItem() {
        return this.getSegmentBlock().asItem();
    }

    public PipeMatrixSegmentBlock getSegmentBlock() {
        if (this.segmentBlock == null) {
            this.segmentBlock = PipeMatrixSegmentBlock.LOOP_TO_MATRIX.get(this);
        }

        return this.segmentBlock;
    }

    @Override
    public void onAdjacentPlaceOnSide(World world, BlockPos pos, BlockState state, BlockPos placedPos, BlockState placedState, ItemPlacementContext context) {
        // if the adjacent placed block would extend this one, replace this block with a pipe matrix
        if (placedState.getBlock() instanceof PipeMatrixUBendBlock && placedState.get(FACING).equals(context.getSide())) {
            world.setBlockState(pos, getSegmentBlock().getDefaultState().with(PipeMatrixSegmentBlock.AXIS, context.getSide().getAxis()));
        }
    }

    @Override
    public ItemActionResult onManualWrenchInteraction(ManualWrenchInteractionContext context) {
        if (getSegmentBlock() != null) {
            BlockPos pos = context.hitResult().getBlockPos();

            context.world().setBlockState(
                    context.hitResult().getBlockPos(),
                    getSegmentBlock().getDefaultState().with(PipeMatrixSegmentBlock.AXIS, context.targetState().get(FACING).getAxis())
            );

            context.world().playSound(
                    context.player(),
                    pos,
                    soundGroup.getPlaceSound(),
                    context.player().getSoundCategory(),
                    0.8f + (0.2f * context.world().getRandom().nextFloat()),
                    0.4f + (0.4f * context.world().getRandom().nextFloat())
            );

            // trip sculk sensors because it's funny
            context.world().emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(context.player(), context.targetState()));

            return ItemActionResult.SUCCESS;
        } else {
            return null;
        }
    }

    @Override
    public boolean onDispenserWrenchInteraction(DispenserWrenchInteractionContext context) {
        if (getSegmentBlock() != null) {
            context.serverWorld().setBlockState(
                    context.targetPos(),
                    getSegmentBlock().getDefaultState().with(PipeMatrixSegmentBlock.AXIS, context.targetState().get(FACING).getAxis())
            );

            context.serverWorld().playSound(
                    null,
                    context.targetPos(),
                    soundGroup.getPlaceSound(),
                    SoundCategory.BLOCKS,
                    0.8f + (0.2f * context.serverWorld().getRandom().nextFloat()),
                    0.4f + (0.4f * context.serverWorld().getRandom().nextFloat())
            );

            // trip sculk sensors because it's funny
            context.serverWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.targetPos(), GameEvent.Emitter.of(context.targetState()));

            return true;
        } else {
            return false;
        }
    }
}
