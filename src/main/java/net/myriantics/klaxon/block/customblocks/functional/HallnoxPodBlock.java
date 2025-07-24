package net.myriantics.klaxon.block.customblocks.functional;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
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
import net.minecraft.util.hit.BlockHitResult;
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
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.worldgen.KlaxonSaplingGenerators;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.jetbrains.annotations.Nullable;

public class HallnoxPodBlock extends SaplingBlock implements LandingBlock, Waterloggable {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty GROWTH_DISABLED = KlaxonBlockStateProperties.GROWTH_DISABLED;

    private static final VoxelShape UP_SHAPE = Block.createCuboidShape(2.0, 2.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 14.0);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(2.0, 2.0, 2.0, 16.0, 14.0, 14.0);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(2.0, 2.0, 2.0, 14.0, 14.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 2.0, 2.0, 14.0, 14.0, 14.0);

    private final int FALLING_DELAY = 2;

    private final DirectionalSaplingGenerator generator;

    public HallnoxPodBlock(DirectionalSaplingGenerator generator, Settings settings) {
        super(KlaxonSaplingGenerators.EMPTY, settings.pistonBehavior(PistonBehavior.DESTROY).ticksRandomly());
        this.generator = generator;
        this.setDefaultState(getDefaultState()
                .with(FACING, Direction.DOWN)
                .with(WATERLOGGED, false)
                .with(GROWTH_DISABLED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, WATERLOGGED, GROWTH_DISABLED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return isSupported(world, pos, state.get(FACING));
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
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState supportingState = world.getBlockState(pos.offset(state.get(FACING)));
        if (!supportingState.isIn(KlaxonBlockTags.HALLNOX_POD_NATURAL_GROWTH_INHIBITING) && random.nextInt(12) == 0) {
            this.generate(world, pos, state, random);
        }
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

    private boolean isSupported(WorldView world, BlockPos pos, Direction facing) {
        BlockPos neighborPos = pos.offset(facing);
        return world.getBlockState(neighborPos).isSideSolid(world, neighborPos, facing, SideShapeType.RIGID);
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (world instanceof ServerWorld serverWorld) {
            BlockPos targetPos = hit.getBlockPos();
            Direction facing = state.get(FACING);

            // try to fall if it's not supported or is not resting on the ground
            if (!facing.equals(Direction.DOWN) || !isSupported(world, targetPos, facing)) {
                tryFall(serverWorld, targetPos, state);
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (!isSupported(world, pos, state.get(FACING))) {
            tryFall(world, pos, state);
        }
    }

    // fall if possible
    // if at the bottom of the world, break block
    private void tryFall(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState) {
        if (blockPos.getY() >= serverWorld.getBottomY()) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(serverWorld, blockPos, blockState);
            // this is because it's funny :)
            fallingBlockEntity.setHurtEntities(1.5f, 10);
        } else {
            serverWorld.breakBlock(blockPos, true);
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

        return state;
    }

    // don't grow when connected to a tree or if it's been sheared
    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return !state.get(GROWTH_DISABLED) && super.canGrow(world, random, pos, state);
    }

    // don't let people waste bonemeal
    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return !state.get(GROWTH_DISABLED);
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
