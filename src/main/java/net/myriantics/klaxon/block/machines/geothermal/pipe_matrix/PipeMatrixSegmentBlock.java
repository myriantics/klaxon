package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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

    public static final Map<PipeMatrixLoopBlock, PipeMatrixSegmentBlock> LOOP_TO_MATRIX = new HashMap<>();

    private final PipeMatrixLoopBlock loopBlock;

    public PipeMatrixSegmentBlock(Settings settings, Block loopBlock) {
        super(settings);
        if (loopBlock instanceof PipeMatrixLoopBlock loop) {
            this.loopBlock = loop;
            LOOP_TO_MATRIX.put(loop, this);
        } else {
            throw new IllegalArgumentException("Construction of PipeMatrixSegmentBlock \"" + this + "\" failed - Argument \"" + loopBlock + "\" does not inherit from PipeMatrixLoopBlock.");
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
        return neighborState instanceof PipeMatrix && neighborState.get(FORMED) ? state : state.with(FORMED, false);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        Direction clickedSide = ctx.getSide();
        BlockPos clickedPos = ctx.getBlockPos().offset(clickedSide);
        BlockState clickedState = world.getBlockState(clickedPos);

        // if this should connect to the target state, delegate to the loop block
        if (clickedState.getBlock() instanceof PipeMatrix pipeMatrix && pipeMatrix.canConnect(clickedState, clickedSide)) {
            return this.loopBlock.getPlacementState(ctx);
        }

        return this.getDefaultState().with(AXIS, clickedSide.getAxis());
    }

    @Override
    public ItemActionResult onWrenched(BlockState targetState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        Direction.Axis axis = targetState.get(AXIS);
        Direction clickedSide = hitResult.getSide();

        Vec3d blockInteractionPos = hitResult.getPos().subtract(Vec3d.of(hitResult.getBlockPos()));

        Direction.AxisDirection clickedAxisDir = blockInteractionPos.getComponentAlongAxis(axis) >= 0.5 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;

        // thats cool i never thought to instantiate an array inside an enhanced for loop before
        // dope
        for (Direction.AxisDirection axisDirection : new Direction.AxisDirection[] {clickedAxisDir, clickedAxisDir.getOpposite()}) {
            Direction neighborDirection = Direction.from(targetState.get(AXIS), axisDirection);
            BlockPos neighborPos = pos.offset(neighborDirection);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof PipeMatrix pipeMatrix && pipeMatrix.canConnect(neighborState, neighborDirection)) {
                BlockState newState = this.loopBlock.getDefaultState().with(PipeMatrixLoopBlock.FACING, neighborDirection);

                world.setBlockState(pos, newState);

                return ItemActionResult.SUCCESS;
            }
        }

        return null;
    }

    @Override
    public ItemActionResult onDispenserWrenched(BlockState targetState, BlockPos targetPos, ItemStack stack, ServerWorld serverWorld, Direction facing, BlockPointer pointer) {
        return null;
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        return direction.getAxis().equals(state.get(AXIS));
    }
}
