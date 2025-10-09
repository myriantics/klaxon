package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.api.Wrenchable;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PipeMatrixSegmentBlock extends Block implements Wrenchable, PipeMatrix {
    // Tracks the axis the pipes run along.
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    // Tracks whether this pipe matrix loop is part of a valid structure or not.
    public static final BooleanProperty FORMED = KlaxonBlockStateProperties.FORMED;

    public static final Map<PipeMatrixUBendBlock, PipeMatrixSegmentBlock> LOOP_TO_MATRIX = new HashMap<>();

    private final PipeMatrixUBendBlock uBendBlock;

    public PipeMatrixSegmentBlock(Settings settings, Block uBendBlock) {
        super(settings);
        if (uBendBlock instanceof PipeMatrixUBendBlock loop) {
            this.uBendBlock = loop;
            LOOP_TO_MATRIX.put(loop, this);
        } else {
            throw new IllegalArgumentException("Construction of PipeMatrixSegmentBlock \"" + this + "\" failed - Argument \"" + uBendBlock + "\" does not inherit from PipeMatrixLoopBlock.");
        }

        this.setDefaultState(this.getDefaultState()
                .with(AXIS, Direction.Axis.Y)
                .with(FORMED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AXIS, FORMED);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return (neighborState instanceof PipeMatrix pipeMatrix
                && direction.getAxis().equals(state.get(AXIS))
                && pipeMatrix.sideHasExposedPipes(neighborState, direction.getOpposite())
                && neighborState.get(FORMED)
        )
                ? state
                : state.with(FORMED, false);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        Direction clickedSide = ctx.getSide();
        BlockPos clickedPos = ctx.getBlockPos().offset(clickedSide);
        BlockState clickedState = world.getBlockState(clickedPos);
        boolean sneaking = ctx.getPlayer() != null && ctx.getPlayer().isSneaking();

        // if this should connect to the target state, delegate to the loop block
        // only do this when sneaking
        if (sneaking && clickedState.getBlock() instanceof PipeMatrix pipeMatrix && pipeMatrix.sideHasExposedPipes(clickedState, clickedSide.getOpposite())) {
            return this.uBendBlock.getPlacementState(ctx);
        }

        return this.getDefaultState().with(AXIS, clickedSide.getAxis());
    }

    @Override
    public ItemActionResult onWrenched(BlockState targetState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        Direction.Axis axis = targetState.get(AXIS);

        Vec3d blockInteractionPos = hitResult.getPos().subtract(Vec3d.of(hitResult.getBlockPos()));

        Direction.AxisDirection clickedAxisDir = blockInteractionPos.getComponentAlongAxis(axis) < 0.5 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;

        BlockState newUBendState = this.uBendBlock.getDefaultState().with(PipeMatrixUBendBlock.FACING, Direction.from(targetState.get(AXIS), clickedAxisDir));

        world.setBlockState(
                pos,
                PipeMatrixUBendBlock.withAxisIfPossible(
                        newUBendState,
                        hitResult.getSide().getAxis(),
                        player.getHorizontalFacing().getAxis()
                ).orElse(newUBendState)
        );

        world.playSound(
                player,
                pos,
                soundGroup.getPlaceSound(),
                player.getSoundCategory(),
                0.8f + (0.2f * world.getRandom().nextFloat()),
                0.4f + (0.4f * world.getRandom().nextFloat())
        );

        // trip sculk sensors because it's funny
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, targetState));

        return ItemActionResult.SUCCESS;
    }

    @Override
    public ItemActionResult onDispenserWrenched(BlockState targetState, BlockPos targetPos, ItemStack stack, ServerWorld serverWorld, Direction facing, BlockPointer pointer) {
        Direction.Axis axis = targetState.get(AXIS);

        // In the future, dispenser wrench behaviors will be randomly generated!

        BlockState newUBendState = this.uBendBlock.getDefaultState();
        if (axis.equals(facing.getAxis())) {
            newUBendState = newUBendState.with(PipeMatrixUBendBlock.FACING, facing);
        } else {
            Direction.AxisDirection axisDirection = serverWorld.getRandom().nextFloat() > 0.5 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;

            for (Direction.AxisDirection dir : new Direction.AxisDirection[] {axisDirection, axisDirection.getOpposite()}) {
                Direction adjacentDirection = Direction.from(axis, dir);
                BlockPos adjacentPos = targetPos.offset(adjacentDirection);
                BlockState adjacentState = serverWorld.getBlockState(adjacentPos);

                if (adjacentState.getBlock() instanceof PipeMatrix pipeMatrix && pipeMatrix.sideHasExposedPipes(adjacentState, adjacentDirection)) {
                    axisDirection = dir;
                    break;
                }
            }

            newUBendState = newUBendState.with(
                    PipeMatrixUBendBlock.FACING,
                    Direction.from(axis, axisDirection)
            );

        }

        // Randomly generated?!?

        serverWorld.setBlockState(
                targetPos,
                PipeMatrixUBendBlock.withAxisIfPossible(
                        newUBendState,
                        axis.equals(facing.getAxis())
                                ? serverWorld.getRandom().nextFloat() > 0.5 ? Direction.Axis.X : Direction.Axis.Z
                                : facing.getAxis()
                ).orElse(newUBendState)
        );

        // Randomly generated!!!

        serverWorld.playSound(
                null,
                targetPos,
                soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS,
                0.8f + (0.2f * serverWorld.getRandom().nextFloat()),
                0.4f + (0.4f * serverWorld.getRandom().nextFloat())
        );

        // trip sculk sensors because it's funny
        serverWorld.emitGameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Emitter.of(targetState));

        return ItemActionResult.SUCCESS;
    }

    @Override
    public boolean sideHasExposedPipes(BlockState state, Direction direction) {
        return direction.getAxis().equals(state.get(AXIS));
    }
}
