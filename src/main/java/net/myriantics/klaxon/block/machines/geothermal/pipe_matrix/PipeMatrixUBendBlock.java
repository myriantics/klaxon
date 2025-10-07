package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.myriantics.klaxon.api.NeighborPlacementListener;
import net.myriantics.klaxon.api.Wrenchable;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class PipeMatrixUBendBlock extends Block implements Wrenchable, PipeMatrix, NeighborPlacementListener {
    // Tracks the axis the pipes turns around.
    // This refers to the axis as if the facing direction was the Y axis.
    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = Properties.HORIZONTAL_AXIS;
    // Tracks the direction the pipe interface point is in.
    public static final DirectionProperty FACING = Properties.FACING;
    // Tracks whether this pipe matrix loop is part of a valid structure or not.
    public static final BooleanProperty FORMED = KlaxonBlockStateProperties.FORMED;

    private Item pickItem;

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

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction clickedDirection = ctx.getSide();
        Direction facingDirection = ctx.getPlayerLookDirection();

        // this method is only called from the pipe matrix block, and we verify that the clicked block is safe there.
        BlockState newState = this.getDefaultState().with(FACING, clickedDirection);

        // hacky switch statement go brr to satisfy my hacky fix for a potential fuckup
        switch (clickedDirection.getAxis()) {
            case X -> {
                switch (facingDirection.getAxis()) {
                    case Y -> {
                        newState = newState.with(HORIZONTAL_AXIS, Direction.Axis.X);
                    }
                    case Z -> {
                        newState = newState.with(HORIZONTAL_AXIS, Direction.Axis.Z);
                    }
                }
            }
            case Y -> {
                switch (facingDirection.getAxis()) {
                    case X -> {
                        newState = newState.with(HORIZONTAL_AXIS, Direction.Axis.X);
                    }
                    case Z -> {
                        newState = newState.with(HORIZONTAL_AXIS, Direction.Axis.Z);
                    }
                }
            }
            case Z -> {
                switch (facingDirection.getAxis()) {
                    case X -> {
                        newState = newState.with(HORIZONTAL_AXIS, Direction.Axis.X);
                    }
                    case Y -> {
                        newState = newState.with(HORIZONTAL_AXIS, Direction.Axis.Z);
                    }
                }
            }
        }

        return newState;
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        if (this.pickItem == null) {
            this.pickItem = PipeMatrixSegmentBlock.LOOP_TO_MATRIX.get(this).asItem();
        }

        return pickItem == null || pickItem.equals(Items.AIR) ? super.getPickStack(world, pos, state) : new ItemStack(pickItem);
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        return direction.getOpposite().equals(state.get(FACING));
    }

    @Override
    public void onAdjacentPlaceOnSide(World world, BlockPos pos, BlockState state, BlockPos placedPos, BlockState placedState, ItemPlacementContext context) {
        // if the adjacent placed block would extend this one, replace this block with a pipe matrix
        if (placedState.getBlock() instanceof PipeMatrixUBendBlock && placedState.get(FACING).equals(context.getSide())) {
            world.setBlockState(pos, PipeMatrixSegmentBlock.LOOP_TO_MATRIX.get(this).getDefaultState().with(PipeMatrixSegmentBlock.AXIS, context.getSide().getAxis()));
        }
    }

    @Override
    public ItemActionResult onWrenched(BlockState targetState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        @Nullable Block segmentBlock = PipeMatrixSegmentBlock.LOOP_TO_MATRIX.get(this);
        if (segmentBlock != null) {
            world.setBlockState(hitResult.getBlockPos(), segmentBlock.getDefaultState().with(PipeMatrixSegmentBlock.AXIS, targetState.get(FACING).getAxis()));
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    public ItemActionResult onDispenserWrenched(BlockState targetState, BlockPos targetPos, ItemStack stack, ServerWorld serverWorld, Direction facing, BlockPointer pointer) {
        @Nullable Block segmentBlock = PipeMatrixSegmentBlock.LOOP_TO_MATRIX.get(this);
        if (segmentBlock != null) {
            serverWorld.setBlockState(targetPos, segmentBlock.getDefaultState().with(PipeMatrixSegmentBlock.AXIS, targetState.get(FACING).getAxis()));
        }
        return ItemActionResult.SUCCESS;
    }
}
