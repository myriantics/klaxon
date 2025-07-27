package net.myriantics.klaxon.block.customblocks.decor;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import org.jetbrains.annotations.Nullable;

public class HallnoxBulbBlock extends ConnectingBlock implements Waterloggable {

    public static final MapCodec<HallnoxBulbBlock> CODEC = createCodec(HallnoxBulbBlock::new);

    private static final float RADIUS = 0.3125f; // 5/16 pixels
    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(2, 2, 2, 14, 14, 14);

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected final VoxelShape[] facingsToFusedShape = new VoxelShape[64];

    public HallnoxBulbBlock(Settings settings) {
        super(RADIUS, settings);
        this.setDefaultState(getDefaultState()
                .with(WATERLOGGED, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
        );
    }


    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int mask = getConnectionMask(state);

        if (facingsToFusedShape[mask] == null) {
            facingsToFusedShape[mask] = VoxelShapes.union(BASE_SHAPE, facingsToShape[mask]);
        }

        return facingsToFusedShape[mask];
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState newState = super.getPlacementState(ctx);

        // only run our edits if
        if (newState != null) {
            World world = ctx.getWorld();
            BlockPos pos = ctx.getBlockPos();
            Direction clickedFace = ctx.getSide();
            Direction offsetDir = clickedFace.getOpposite();
            PlayerEntity player = ctx.getPlayer();

            newState = newState.with(WATERLOGGED, world.getFluidState(pos).isOf(Fluids.WATER.getStill()));

            // connect to blocks with solid center unless holding shift
            BlockState clickedState = world.getBlockState(pos.offset(offsetDir));
            if (player != null && !player.isSneaking() && clickedState.isSideSolid(world, pos, clickedFace, SideShapeType.CENTER)) {
                newState = newState.with(FACING_PROPERTIES.get(offsetDir), true);
            }

            // automatically connect to other hallnox bulbs - updates them to connect, too
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                BlockPos neighborPos = pos.offset(direction);
                BlockState neighborState = world.getBlockState(neighborPos);
                if (neighborState.getBlock() instanceof HallnoxBulbBlock) {
                    newState = newState.with(FACING_PROPERTIES.get(direction), true);
                    world.setBlockState(neighborPos, neighborState.with(FACING_PROPERTIES.get(direction.getOpposite()), true));
                }
            }
        }

        return newState;
    }

    @Override
    protected MapCodec<HallnoxBulbBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }
}
