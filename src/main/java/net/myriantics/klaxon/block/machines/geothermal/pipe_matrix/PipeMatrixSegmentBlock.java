package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PipeMatrixSegmentBlock extends Block implements Wrenchable, PipeMatrix {
    // Tracks the axis the pipes run along.
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    // Tracks whether this pipe matrix loop is part of a valid structure or not.
    public static final BooleanProperty FORMED = KlaxonBlockStateProperties.FORMED;

    public static final Map<PipeMatrixUBendBlock, PipeMatrixSegmentBlock> LOOP_TO_MATRIX = new HashMap<>();

    protected static final WrenchInteraction CONNECT = WrenchInteraction.of(KlaxonWrenchActionTypes.CONNECT, PipeMatrixSegmentBlock::handleConnect);
    protected static final WrenchInteractionMap SOLID_MAP = CONNECT.toSingletonMap();
    protected static final WrenchInteractionMap SPLIT_MAP = WrenchInteractionMap.split(CONNECT, CONNECT);

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

    private static Optional<InteractionResult> handleConnect(WrenchActionContext context) {
        Level level = context.level();
        BlockState state = context.getTargetState();
        BlockPos pos = context.getTargetPos();
        RandomSource random = level.getRandom();

        if (!(state.getBlock() instanceof PipeMatrixSegmentBlock pipeMatrixSegmentBlock)) {
            throw new AssertionError();
        }

        Direction.Axis axis = state.getValue(AXIS);

        // massive ugly code block go brrr
        BlockState newUBendState = switch (context) {
            case WrenchActionContext.Dispenser dispenser -> {
                BlockState temp = pipeMatrixSegmentBlock.uBendBlock.defaultBlockState();
                Direction dispenserFacing = dispenser.getDispenserFacing();
                if (axis.equals(dispenserFacing.getAxis())) {
                    yield temp.setValue(PipeMatrixUBendBlock.FACING, dispenserFacing);
                } else {
                    Direction.AxisDirection axisDirection = random.nextFloat() > 0.5 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;

                    for (Direction.AxisDirection dir : new Direction.AxisDirection[] {axisDirection, axisDirection.opposite()}) {
                        Direction adjacentDirection = Direction.fromAxisAndDirection(axis, dir);
                        BlockPos adjacentPos = pos.relative(adjacentDirection);
                        BlockState adjacentState = level.getBlockState(adjacentPos);

                        if (adjacentState.getBlock() instanceof PipeMatrix pipeMatrix && pipeMatrix.sideHasExposedPipes(adjacentState, adjacentDirection)) {
                            axisDirection = dir;
                            break;
                        }
                    }

                    yield temp.setValue(
                            PipeMatrixUBendBlock.FACING,
                            Direction.fromAxisAndDirection(axis, axisDirection)
                    );
                }
            }
            case WrenchActionContext.Manual manual -> {
                Vec3 blockInteractionPos = manual.getHitResult().getLocation().subtract(Vec3.atLowerCornerOf(pos));
                Direction.AxisDirection clickedAxisDir = blockInteractionPos.get(axis) < 0.5 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
                yield pipeMatrixSegmentBlock.uBendBlock.defaultBlockState().setValue(PipeMatrixUBendBlock.FACING, Direction.fromAxisAndDirection(axis, clickedAxisDir));
            }
        };

        if (!level.isClientSide()) {
            level.setBlockAndUpdate(
                    pos,
                    switch (context) {
                        case WrenchActionContext.Dispenser dispenser -> {
                            Direction.Axis dispenserAxis = dispenser.getDispenserFacing().getAxis();
                            yield PipeMatrixUBendBlock.withAxisIfPossible(
                                    newUBendState,
                                    axis.equals(dispenserAxis)
                                            ? context.level().getRandom().nextFloat() > 0.5 ? Direction.Axis.X : Direction.Axis.Z
                                            : dispenserAxis
                            ).orElse(newUBendState);
                        }
                        case WrenchActionContext.Manual manual -> {
                            yield PipeMatrixUBendBlock.withAxisIfPossible(
                                    newUBendState,
                                    manual.getHitResult().getDirection().getAxis(),
                                    manual.getPlayer().getDirection().getAxis()
                            ).orElse(newUBendState);
                        }
                    }
            );
        }

        level.playSound(
                context instanceof WrenchActionContext.Manual manual ? manual.getPlayer() : null,
                pos,
                pipeMatrixSegmentBlock.soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                0.8f + (0.2f * level.getRandom().nextFloat()),
                0.4f + (0.4f * level.getRandom().nextFloat())
        );

        switch (context) {
            case WrenchActionContext.Dispenser dispenser -> level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
            case WrenchActionContext.Manual manual -> level.gameEvent(manual.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
        }

        return Optional.of(InteractionResult.SUCCESS);
    }

    @Override
    public boolean sideHasExposedPipes(BlockState state, Direction direction) {
        return direction.getAxis().equals(state.getValue(AXIS));
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return context.getTargetState().getValue(AXIS).equals(context.getHitResult().getDirection().getAxis())
                ? SOLID_MAP
                : SPLIT_MAP;
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return CONNECT;
    }
}
