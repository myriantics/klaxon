package net.myriantics.klaxon.block.functional.hallnox_pod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.misc.KlaxonParticleTypes;
import net.myriantics.klaxon.registry.worldgen.KlaxonSaplingGenerators;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HallnoxPodBlock extends SaplingBlock implements Fallable, SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty GROWTH_DISABLED = KlaxonBlockStateProperties.GROWTH_DISABLED;

    private static final VoxelShape UP_SHAPE = Block.box(2.0, 2.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape DOWN_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);
    private static final VoxelShape NORTH_SHAPE = Block.box(2.0, 2.0, 0.0, 14.0, 14.0, 14.0);
    private static final VoxelShape EAST_SHAPE = Block.box(2.0, 2.0, 2.0, 16.0, 14.0, 14.0);
    private static final VoxelShape SOUTH_SHAPE = Block.box(2.0, 2.0, 2.0, 14.0, 14.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.box(0.0, 2.0, 2.0, 14.0, 14.0, 14.0);

    private final int FALLING_DELAY = 2;

    private final DirectionalSaplingGenerator generator;

    public HallnoxPodBlock(DirectionalSaplingGenerator generator, Properties settings) {
        super(KlaxonSaplingGenerators.EMPTY, settings.pushReaction(PushReaction.DESTROY).randomTicks());
        this.generator = generator;
        this.registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.DOWN)
                .setValue(WATERLOGGED, false)
                .setValue(GROWTH_DISABLED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED, GROWTH_DISABLED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return isSupported(world, pos, state.getValue(FACING));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockState supportingState = world.getBlockState(pos.relative(state.getValue(FACING)));
        if (!state.getValue(GROWTH_DISABLED) && !supportingState.is(KlaxonBlockTags.HALLNOX_POD_NATURAL_GROWTH_INHIBITING) && random.nextInt(12) == 0) {
            this.advanceTree(world, pos, state, random);
        }
    }

    @Override
    public void onLand(Level world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        Fallable.super.onLand(world, pos, fallingBlockState, currentStateInPos, fallingBlockEntity);

        BlockState newState = fallingBlockState;

        // if it lands properly, it should be sitting on the floor.
        if (fallingBlockState.is(KlaxonBlocks.HALLNOX_POD)) {
            newState = newState.setValue(FACING, Direction.DOWN);
        }

        // no nether water fuckery for you
        if (world.dimensionType().ultraWarm()) {
            newState = newState.setValue(WATERLOGGED, false);
        }

        // if we've made changes, update block state
        if (!newState.equals(fallingBlockState)) world.setBlockAndUpdate(pos, newState);
    }

    @Override
    public DamageSource getFallDamageSource(Entity attacker) {
        Optional<Holder.Reference<DamageType>> domed = attacker.damageSources().damageTypes.getHolder(KlaxonDamageTypes.HALLNOX_POD_DOMED);
        return domed.isPresent()
                ? new DamageSource(domed.get(), attacker)
                : Fallable.super.getFallDamageSource(attacker);
    }

    @Override
    public void advanceTree(ServerLevel world, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            world.setBlock(pos, state.cycle(STAGE), Block.UPDATE_INVISIBLE);
        } else {
            this.generator.generate(state.getValue(FACING).getOpposite(), world, world.getChunkSource().getGenerator(), pos, state, random);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState original = super.getStateForPlacement(ctx);

        if (original != null) {
            Level world = ctx.getLevel();
            BlockPos pos = ctx.getClickedPos();
            Direction facing = ctx.getClickedFace().getOpposite();
            boolean waterlogged = world.getFluidState(pos).is(Fluids.WATER.getSource());

            BlockState newState = original.setValue(WATERLOGGED, waterlogged);

            // try placing on clicked side first
            if (isSupported(world, pos, facing)) return newState.setValue(FACING, facing);

            Direction playerFacing = ctx.getHorizontalDirection();
            // next, try placing on player facing and its opposite.
            if (!facing.equals(playerFacing) && isSupported(world, pos, playerFacing)) return newState.setValue(FACING, playerFacing);
            if (isSupported(world, pos, playerFacing.getOpposite())) return newState.setValue(FACING, playerFacing.getOpposite());

            // try placing in all possible orientations
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                // don't check ones we've already checked
                if (direction.equals(facing) || direction.equals(playerFacing) || direction.equals(playerFacing.getOpposite())) continue;
                if (isSupported(world, pos, direction)) return newState.setValue(FACING, direction);
            }
        }

        // if all checks fail, we fail to place.
        return null;
    }

    private boolean isSupported(LevelReader world, BlockPos pos, Direction facing) {
        BlockPos neighborPos = pos.relative(facing);
        return world.getBlockState(neighborPos).isFaceSturdy(world, neighborPos, facing.getOpposite(), SupportType.RIGID);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (!isSupported(world, pos, state.getValue(FACING))) {
            tryFall(world, pos, state);
        }
    }

    // fall if possible
    // if at the bottom of the world, break block
    private void tryFall(ServerLevel serverWorld, BlockPos blockPos, BlockState blockState) {
        if (blockPos.getY() >= serverWorld.getMinBuildHeight()) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(serverWorld, blockPos, blockState);
            // this is because it's funny :)
            fallingBlockEntity.setHurtsEntities(1.5f, 10);
        } else {
            serverWorld.destroyBlock(blockPos, true);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        world.scheduleTick(pos, this, FALLING_DELAY);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        BlockPos offsetPos = pos.relative(facing);

        // only schedule block tick if updater is the supporting block
        if (!world.isClientSide() && offsetPos.equals(neighborPos)) world.scheduleTick(pos, this, FALLING_DELAY);

        return state;
    }

    // don't grow when connected to a tree or if it's been sheared
    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return !state.getValue(GROWTH_DISABLED) && super.isBonemealSuccess(world, random, pos, state);
    }

    // don't let people waste bonemeal
    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return !state.getValue(GROWTH_DISABLED);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(8) == 0) {
            Direction facing = state.getValue(FACING);

            BlockPos blockPos = pos.below();
            if (!isSupported(world, pos, facing) && FallingBlock.isFree(world.getBlockState(blockPos))) {
                spawnParticle(world, pos, random, facing);
            }
        }
    }

    private void spawnParticle(Level world, BlockPos pos, RandomSource random, Direction facing) {

        // make sure we're not emitting particles upwards
        if (!facing.equals(Direction.UP)) {
            Vec3 centerPos = pos.getCenter();
            Vec3i facingVector = facing.getNormal();

            // make sure drips only originate from proper area on model
            float bound = (12f/16 - 4f/16) / 2;

            double particleX = centerPos.x() + (bound * random.nextFloat() * (random.nextBoolean() ? 1 : -1));
            double particleY = centerPos.y() + (bound * random.nextFloat() * (random.nextBoolean() ? 1 : -1));
            double particleZ = centerPos.z() + (bound * random.nextFloat() * (random.nextBoolean() ? 1 : -1));

            switch (facing.getAxis()) {
                case X -> {
                    particleX = centerPos.x() + (0.55 * facingVector.getX());
                }
                case Y -> {
                    particleY = centerPos.y() + (0.65 * facingVector.getY());
                }
                case Z -> {
                    particleZ = centerPos.z() + (0.55 * facingVector.getZ());
                }
            }

            world.addParticle(KlaxonParticleTypes.HALLNOX_POD_DRIP.value(), particleX, particleY, particleZ, 0.0, 0.0, 0.0);
        }
    }
}
