package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PipeMatrixSegmentBlock extends Block implements Wrenchable, PipeMatrix {
    // Tracks the axis the pipes run along.
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    // Tracks whether this pipe matrix loop is part of a valid structure or not.
    public static final BooleanProperty FORMED = KlaxonBlockStateProperties.FORMED;

    public static final Map<PipeMatrixUBendBlock, PipeMatrixSegmentBlock> LOOP_TO_MATRIX = new HashMap<>();

    private final PipeMatrixUBendBlock uBendBlock;

    public PipeMatrixSegmentBlock(Properties settings, Block uBendBlock) {
        super(settings);
        if (uBendBlock instanceof PipeMatrixUBendBlock loop) {
            this.uBendBlock = loop;
            LOOP_TO_MATRIX.put(this.uBendBlock, this);
        } else {
            throw new IllegalArgumentException("Construction of PipeMatrixSegmentBlock \"" + this + "\" failed - Argument \"" + uBendBlock + "\" does not inherit from PipeMatrixLoopBlock.");
        }

        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(FORMED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS, FORMED);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return (neighborState instanceof PipeMatrix pipeMatrix
                && direction.getAxis().equals(state.getValue(AXIS))
                && pipeMatrix.sideHasExposedPipes(neighborState, direction.getOpposite())
                && neighborState.getValue(FORMED)
        )
                ? state
                : state.setValue(FORMED, false);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        Direction clickedSide = ctx.getClickedFace();
        BlockPos clickedPos = ctx.getClickedPos().relative(clickedSide);
        BlockState clickedState = world.getBlockState(clickedPos);
        boolean sneaking = ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown();

        // if this should connect to the target state, delegate to the loop block
        // only do this when sneaking
        if (sneaking && clickedState.getBlock() instanceof PipeMatrix pipeMatrix && pipeMatrix.sideHasExposedPipes(clickedState, clickedSide.getOpposite())) {
            return this.uBendBlock.getStateForPlacement(ctx);
        }

        return this.defaultBlockState().setValue(AXIS, clickedSide.getAxis());
    }

    @Override
    public InteractionResult onManualWrenchInteraction(ManualWrenchInteractionContext context) {
        BlockPos pos = context.hitResult().getBlockPos();
        Direction.Axis axis = context.targetState().getValue(AXIS);

        Vec3 blockInteractionPos = context.hitResult().getLocation().subtract(Vec3.atLowerCornerOf(pos));

        Direction.AxisDirection clickedAxisDir = blockInteractionPos.get(axis) < 0.5 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;

        BlockState newUBendState = this.uBendBlock.defaultBlockState().setValue(PipeMatrixUBendBlock.FACING, Direction.fromAxisAndDirection(context.targetState().getValue(AXIS), clickedAxisDir));

        context.world().setBlockAndUpdate(
                pos,
                PipeMatrixUBendBlock.withAxisIfPossible(
                        newUBendState,
                        context.hitResult().getDirection().getAxis(),
                        context.player().getDirection().getAxis()
                ).orElse(newUBendState)
        );

        context.world().playSound(
                context.player(),
                pos,
                soundType.getPlaceSound(),
                context.player().getSoundSource(),
                0.8f + (0.2f * context.world().getRandom().nextFloat()),
                0.4f + (0.4f * context.world().getRandom().nextFloat())
        );

        // trip sculk sensors because it's funny
        context.world().gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(context.player(), context.targetState()));

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onDispenserWrenchInteraction(DispenserWrenchInteractionContext context) {
        Direction dispenserFacing = context.dispenserFacing();
        Direction.Axis axis = context.targetState().getValue(AXIS);

        // In the future, dispenser wrench behaviors will be randomly generated!

        BlockState newUBendState = this.uBendBlock.defaultBlockState();
        if (axis.equals(dispenserFacing.getAxis())) {
            newUBendState = newUBendState.setValue(PipeMatrixUBendBlock.FACING, dispenserFacing);
        } else {
            Direction.AxisDirection axisDirection = context.serverWorld().getRandom().nextFloat() > 0.5 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;

            for (Direction.AxisDirection dir : new Direction.AxisDirection[] {axisDirection, axisDirection.opposite()}) {
                Direction adjacentDirection = Direction.fromAxisAndDirection(axis, dir);
                BlockPos adjacentPos = context.targetPos().relative(adjacentDirection);
                BlockState adjacentState = context.serverWorld().getBlockState(adjacentPos);

                if (adjacentState.getBlock() instanceof PipeMatrix pipeMatrix && pipeMatrix.sideHasExposedPipes(adjacentState, adjacentDirection)) {
                    axisDirection = dir;
                    break;
                }
            }

            newUBendState = newUBendState.setValue(
                    PipeMatrixUBendBlock.FACING,
                    Direction.fromAxisAndDirection(axis, axisDirection)
            );

        }

        // Randomly generated?!?

        context.serverWorld().setBlockAndUpdate(
                context.targetPos(),
                PipeMatrixUBendBlock.withAxisIfPossible(
                        newUBendState,
                        axis.equals(dispenserFacing.getAxis())
                                ? context.serverWorld().getRandom().nextFloat() > 0.5 ? Direction.Axis.X : Direction.Axis.Z
                                : dispenserFacing.getAxis()
                ).orElse(newUBendState)
        );

        // Randomly generated!!!

        context.serverWorld().playSound(
                null,
                context.targetPos(),
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                0.8f + (0.2f * context.serverWorld().getRandom().nextFloat()),
                0.4f + (0.4f * context.serverWorld().getRandom().nextFloat())
        );

        // trip sculk sensors because it's funny
        context.serverWorld().gameEvent(GameEvent.BLOCK_CHANGE, context.targetPos(), GameEvent.Context.of(context.targetState()));

        return true;
    }

    @Override
    public boolean sideHasExposedPipes(BlockState state, Direction direction) {
        return direction.getAxis().equals(state.getValue(AXIS));
    }
}
