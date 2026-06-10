package net.myriantics.klaxon.block.machines.duct.segment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.block.machines.duct.driver.aio.AIODuctDriverBlockEntity;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;
import net.myriantics.klaxon.mechanics.logistics.itemduct.IDuctNodeBlock;
import org.jetbrains.annotations.Nullable;

public class AxisDuctSegmentBlock extends RotatedPillarBlock implements EntityBlock, IDuctNodeBlock {

    private static final VoxelShape Y_AXIS_SHAPE = Block.box(
            2, 0, 2,
            14, 16, 14
    );
    private static final VoxelShape X_AXIS_SHAPE = Block.box(
            0, 2, 2,
            16, 14, 14
    );
    private static final VoxelShape Z_AXIS_SHAPE = Block.box(
            2, 2, 0,
            14, 14, 16
    );

    public static Property<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public AxisDuctSegmentBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canConnectionOpen(BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public boolean isConnectionOpen(BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public BlockState setConnectionForFace(BlockState original, Direction face, boolean connected) {
        return original;
    }

    @Override
    public DuctNode getNode(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity) {
        return blockEntity instanceof DuctSegmentBlockEntity segmentBlockEntity ? segmentBlockEntity : null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DuctSegmentBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        Direction.Axis axis = state.getValue(AXIS);
        for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
            Direction direction = Direction.fromAxisAndDirection(axis, axisDirection);
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof IDuctNodeBlock nodeBlock && nodeBlock.canConnectionOpen(neighborState, direction.getOpposite()) && !nodeBlock.isConnectionOpen(neighborState, direction.getOpposite())) {
                level.setBlockAndUpdate(neighborPos, nodeBlock.setConnectionForFace(neighborState, direction.getOpposite(), true));
            }
        }

        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide()) {
            Direction updatedDirection = Direction.fromDelta(
                    neighborPos.getX() - pos.getX(),
                    neighborPos.getY() - pos.getY(),
                    neighborPos.getZ() - pos.getZ()
            );
            Direction.Axis axis = state.getValue(AXIS);
            if (updatedDirection != null && updatedDirection.getAxis() == axis && level.getBlockEntity(pos) instanceof DuctSegmentBlockEntity segmentBlockEntity) {
                segmentBlockEntity.updateDirections(updatedDirection, true);
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case X -> X_AXIS_SHAPE;
            case Y -> Y_AXIS_SHAPE;
            case Z -> Z_AXIS_SHAPE;
        };
    }
}
