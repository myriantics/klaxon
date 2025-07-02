package net.myriantics.klaxon.block.customblocks.decor;

import net.minecraft.block.*;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import org.jetbrains.annotations.Nullable;

public class HallnoxPodBlock extends SaplingBlock implements LandingBlock, Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING = Properties.FACING;

    private final int FALLING_DELAY = 2;

    public HallnoxPodBlock(SaplingGenerator generator, Settings settings) {
        super(generator, settings);
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
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState original = super.getPlacementState(ctx);

        if (original != null) {
            World world = ctx.getWorld();
            BlockPos pos = ctx.getBlockPos();
            Direction facing = ctx.getSide().getOpposite();
            boolean waterlogged = world.getFluidState(pos).isOf(Fluids.WATER.getStill());

            BlockState newState = original.with(FACING, facing).with(WATERLOGGED, waterlogged);

            // make sure placement is valid before doing anything
            return isSupported(world, pos, facing) ? newState : null;
        } else {
            return null;
        }
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
            if (FallingBlock.canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
                FallingBlockEntity.spawnFromBlock(world, pos, state);
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

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(16) == 0) {
            BlockPos blockPos = pos.down();
            if (FallingBlock.canFallThrough(world.getBlockState(blockPos))) {
                ParticleUtil.spawnParticle(world, pos, random, new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state));
            }
        }
    }
}
