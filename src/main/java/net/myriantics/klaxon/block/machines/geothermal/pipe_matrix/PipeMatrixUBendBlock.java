package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.myriantics.klaxon.block.decor.hallnox_bulb.NeighborPlacementListener;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PipeMatrixUBendBlock extends Block implements Wrenchable, PipeMatrix, NeighborPlacementListener {
    // Tracks the axis the pipes turns around.
    // This refers to the axis as if the facing direction was the Y axis.
    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    // Tracks the direction the pipe interface point is in.
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    // Tracks whether this pipe matrix loop is part of a valid structure or not.
    public static final BooleanProperty FORMED = KlaxonBlockStateProperties.FORMED;

    protected static final WrenchInteraction DISCONNECT_INTERACTION = WrenchInteraction.of(KlaxonWrenchActionTypes.DISCONNECT, context -> handleDisconnect(context));

    private PipeMatrixSegmentBlock segmentBlock;

    public PipeMatrixUBendBlock(Properties settings) {
        super(settings);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(HORIZONTAL_AXIS, Direction.Axis.X)
                .setValue(FACING, Direction.UP)
                .setValue(FORMED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_AXIS, FACING, FORMED);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return neighborState instanceof PipeMatrix && neighborState.getValue(FORMED) ? state : state.setValue(FORMED, false);
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

        switch (state.getValue(FACING).getAxis()) {
            case X -> {
                switch (axis) {
                    case Y -> result = Optional.of(state.setValue(HORIZONTAL_AXIS, Direction.Axis.X));
                    case Z -> result = Optional.of(state.setValue(HORIZONTAL_AXIS, Direction.Axis.Z));
                }
            }
            case Y -> {
                switch (axis) {
                    case X -> result = Optional.of(state.setValue(HORIZONTAL_AXIS, Direction.Axis.X));
                    case Z -> result = Optional.of(state.setValue(HORIZONTAL_AXIS, Direction.Axis.Z));
                }
            }
            case Z -> {
                switch (axis) {
                    case X -> result = Optional.of(state.setValue(HORIZONTAL_AXIS, Direction.Axis.X));
                    case Y -> result = Optional.of(state.setValue(HORIZONTAL_AXIS, Direction.Axis.Z));
                }
            }
        }

        return result;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction clickedDirection = ctx.getClickedFace();
        Direction facingDirection = ctx.getNearestLookingDirection();

        // this method is only called from the pipe matrix block, and we verify that the clicked block is safe there.
        BlockState newState = this.defaultBlockState().setValue(FACING, clickedDirection);

        return withAxisIfPossible(
                newState,
                clickedDirection.getAxis(),
                facingDirection.getAxis()
        ).orElse(newState);
    }

    @Override
    public boolean sideHasExposedPipes(BlockState state, Direction direction) {
        return direction.equals(state.getValue(FACING));
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
    public void onAdjacentPlaceOnSide(Level world, BlockPos pos, BlockState state, BlockPos placedPos, BlockState placedState, BlockPlaceContext context) {
        // if the adjacent placed block would extend this one, replace this block with a pipe matrix
        if (placedState.getBlock() instanceof PipeMatrixUBendBlock && placedState.getValue(FACING).equals(context.getClickedFace())) {
            world.setBlockAndUpdate(pos, getSegmentBlock().defaultBlockState().setValue(PipeMatrixSegmentBlock.AXIS, context.getClickedFace().getAxis()));
        }
    }

    private static Optional<InteractionResult> handleDisconnect(WrenchActionContext context) {
        Level level = context.level();
        BlockState state = context.getTargetState();
        BlockPos pos = context.getTargetPos();

        if (!(state.getBlock() instanceof PipeMatrixUBendBlock uBendBlock)) {
            throw new AssertionError();
        }

        @Nullable PipeMatrixSegmentBlock segmentBlock = uBendBlock.getSegmentBlock();

        if (segmentBlock != null) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, segmentBlock.defaultBlockState().setValue(PipeMatrixSegmentBlock.AXIS, state.getValue(FACING).getAxis()));
            }
            level.playSound(
                    context instanceof WrenchActionContext.Manual manual ? manual.getPlayer() : null,
                    pos,
                    uBendBlock.soundType.getPlaceSound(),
                    SoundSource.BLOCKS,
                    0.8f + (0.2f * level.getRandom().nextFloat()),
                    0.4f + (0.4f * level.getRandom().nextFloat())
            );
            switch (context) {
                case WrenchActionContext.Manual manual -> level.gameEvent(manual.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
                case WrenchActionContext.Dispenser dispenser -> level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
            }

            return Optional.of(InteractionResult.SUCCESS);
        } else {
            return Optional.of(InteractionResult.FAIL);
        }
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return DISCONNECT_INTERACTION.toSingletonMap();
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return DISCONNECT_INTERACTION;
    }
}
