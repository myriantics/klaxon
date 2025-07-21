package net.myriantics.klaxon.block.customblocks.functional;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.NeighborUpdater;
import net.myriantics.klaxon.api.DirectionalSaplingGenerator;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonSaplingGenerators;
import org.jetbrains.annotations.Nullable;

public class HallnoxPodBlock extends SaplingBlock implements LandingBlock, Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING = Properties.FACING;

    private static final VoxelShape UP_SHAPE = Block.createCuboidShape(2.0, 2.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 14.0);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(2.0, 2.0, 2.0, 16.0, 14.0, 14.0);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(2.0, 2.0, 2.0, 14.0, 14.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 2.0, 2.0, 14.0, 14.0, 14.0);

    private final int FALLING_DELAY = 2;

    private final DirectionalSaplingGenerator generator;

    public HallnoxPodBlock(DirectionalSaplingGenerator generator, Settings settings) {
        super(KlaxonSaplingGenerators.EMPTY, settings.pistonBehavior(PistonBehavior.DESTROY));
        this.generator = generator;
        this.setDefaultState(getDefaultState().with(WATERLOGGED, false).with(FACING, Direction.DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED, FACING);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        LandingBlock.super.onLanding(world, pos, fallingBlockState, currentStateInPos, fallingBlockEntity);

        BlockState newState = fallingBlockState;

        // if it lands properly, it should be sitting on the floor.
        if (fallingBlockState.isOf(KlaxonBlocks.HALLNOX_POD)) {
            newState = newState.with(FACING, Direction.DOWN);
        }

        // no nether water fuckery for you
        if (world.getDimension().ultrawarm()) {
            newState = newState.with(WATERLOGGED, false);
        }

        // break if we fall on an incompatible block
        if (!isSupported(world, pos, Direction.DOWN)) {
            world.breakBlock(pos, true);
        }

        // if we've made changes, update block state
        if (!newState.equals(fallingBlockState)) world.setBlockState(pos, newState);
    }

    @Override
    public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        if (state.get(STAGE) == 0) {
            world.setBlockState(pos, state.cycle(STAGE), Block.NO_REDRAW);
        } else {
            this.generator.generate(state.get(FACING).getOpposite(), world, world.getChunkManager().getChunkGenerator(), pos, state, random);
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState original = super.getPlacementState(ctx);

        if (original != null) {
            World world = ctx.getWorld();
            BlockPos pos = ctx.getBlockPos();
            Direction facing = ctx.getSide().getOpposite();
            boolean waterlogged = world.getFluidState(pos).isOf(Fluids.WATER.getStill());

            BlockState newState = original.with(WATERLOGGED, waterlogged);

            // try placing on clicked side first
            if (isSupported(world, pos, facing)) return newState.with(FACING, facing);

            Direction playerFacing = ctx.getHorizontalPlayerFacing();
            // next, try placing on player facing and its opposite.
            if (!facing.equals(playerFacing) && isSupported(world, pos, playerFacing)) return newState.with(FACING, playerFacing);
            if (isSupported(world, pos, playerFacing.getOpposite())) return newState.with(FACING, playerFacing.getOpposite());

            // try placing in all possible orientations
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                // don't check ones we've already checked
                if (direction.equals(facing) || direction.equals(playerFacing) || direction.equals(playerFacing.getOpposite())) continue;
                if (isSupported(world, pos, direction)) return newState.with(FACING, direction);
            }
        }

        // if all checks fail, we fail to place.
        return null;
    }

    private boolean isSupported(WorldAccess world, BlockPos pos, Direction facing) {
        BlockPos neighborPos = pos.offset(facing);
        return world.getBlockState(neighborPos).isSideSolid(world, neighborPos, facing, SideShapeType.RIGID);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        Direction facing = state.get(FACING);

        // fall if it can, otherwise break if unsupported
        if (!isSupported(world, pos, facing)) {
            if (pos.getY() >= world.getBottomY()) {
                FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
                // this is because it's funny :)
                fallingBlockEntity.setHurtEntities(1.5f, 10);
            } else {
                world.breakBlock(pos, true);
            }
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.scheduleBlockTick(pos, this, FALLING_DELAY);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        BlockPos offsetPos = pos.offset(facing);

        // only schedule block tick if updater is the supporting block
        if (!world.isClient() && offsetPos.equals(neighborPos)) world.scheduleBlockTick(pos, this, FALLING_DELAY);

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    // don't grow when connected to a tree or if it's been sheared
    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return super.canGrow(world, random, pos, state);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(16) == 0) {
            BlockPos blockPos = pos.down();
            if (!isSupported(world, pos, state.get(FACING)) && FallingBlock.canFallThrough(world.getBlockState(blockPos))) {
                ParticleUtil.spawnParticle(world, pos, random, new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state));
            }
        }
    }
}
