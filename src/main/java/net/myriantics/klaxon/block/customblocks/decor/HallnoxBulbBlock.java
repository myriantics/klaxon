package net.myriantics.klaxon.block.customblocks.decor;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.Wrenchable;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import org.jetbrains.annotations.Nullable;

public class HallnoxBulbBlock extends ConnectingBlock implements Waterloggable, Wrenchable {

    public static final MapCodec<HallnoxBulbBlock> CODEC = createCodec(HallnoxBulbBlock::new);

    private static final float RADIUS = 0.3125f; // 5/16 pixels
    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(2, 2, 2, 14, 14, 14);
    private static final VoxelShape CORE_SHAPE = Block.createCuboidShape(4, 4, 4, 12, 12, 12);

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

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return CORE_SHAPE;
    }

    protected boolean shouldConnect(World world, BlockState targetState, BlockPos targetPos, Direction offsetDir) {
        return targetState.isSideSolid(world, targetPos, offsetDir, SideShapeType.CENTER) || targetState.getBlock() instanceof HallnoxBulbBlock;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();

            BlockState placementState = block.getPlacementState(new ItemPlacementContext(player, hand, stack, hit));

            if (
                    placementState != null
                    && shouldConnect(world, placementState, pos.offset(hit.getSide()), hit.getSide().getOpposite())
            ) {
                world.setBlockState(pos, state.with(FACING_PROPERTIES.get(hit.getSide()), true));
            }
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public ItemActionResult onWrenched(BlockState targetState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        BlockPos targetPos = hitResult.getBlockPos();

        Vec3d hitPos = hitResult.getPos().subtract(Vec3d.ofCenter(targetPos));
        Direction togglingDirection = Direction.getFacing(hitPos.getX(), hitPos.getY(), hitPos.getZ());

        BlockPos conjoiningPos = targetPos.offset(togglingDirection);
        BlockState conjoiningState = world.getBlockState(conjoiningPos);

        BooleanProperty toggledProperty = FACING_PROPERTIES.get(togglingDirection);
        BooleanProperty conjoiningToggledProperty = FACING_PROPERTIES.get(togglingDirection.getOpposite());

        // don't update block states on the client
        if (!world.isClient()) {
            // cycle conjoining bulb connector state if possible
            if (
                // make sure conjoining state is also a hallnox bulb
                    conjoiningState.getBlock() instanceof HallnoxBulbBlock
                            // make sure the states match
                            && conjoiningState.get(conjoiningToggledProperty)
                            .equals(targetState.get(toggledProperty)
                            )
            ) {
                world.setBlockState(
                        conjoiningPos,
                        conjoiningState.cycle(conjoiningToggledProperty)
                );
            }

            // cycle bulb connector state
            world.setBlockState(
                    targetPos,
                    targetState.cycle(toggledProperty)
            );
        }


        BlockSoundGroup soundGroup = KlaxonBlocks.STEEL_PLATING_BLOCK.getDefaultState().getSoundGroup();

        world.playSound(
                player,
                targetPos,
                targetState.get(toggledProperty) ? soundGroup.getBreakSound() : soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS,
                0.6f + (0.2f + world.getRandom().nextFloat()),
                0.2f + (0.4f + world.getRandom().nextFloat())
        );

        // this is a stub implementation in ClientWorld so it's fine
        // trip sculk sensors because it's funny
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Emitter.of(player, targetState));

        return ItemActionResult.SUCCESS;
    }

    @Override
    public ItemActionResult onDispenserWrenched(BlockState targetState, BlockPos targetPos, ItemStack stack, ServerWorld serverWorld, Direction facing, BlockPointer pointer) {
        BooleanProperty toggledProperty = FACING_PROPERTIES.get(facing.getOpposite());

        // calculations are simpler here because dispensers can only face 6 ways - also can't click on random parts of the block
        serverWorld.setBlockState(targetPos, targetState.cycle(toggledProperty));

        BlockSoundGroup soundGroup = KlaxonBlocks.STEEL_PLATING_BLOCK.getDefaultState().getSoundGroup();
        serverWorld.playSound(
                null,
                targetPos,
                targetState.get(toggledProperty) ? soundGroup.getBreakSound() : soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS,
                0.6f + (0.2f + serverWorld.getRandom().nextFloat()),
                0.2f + (0.4f + serverWorld.getRandom().nextFloat())
        );

        // proc sculk sensors
        serverWorld.emitGameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Emitter.of(targetState));

        return ItemActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        // only update neighbors on placement if placement was actually successful - don't do it if sneaking
        if (placer == null || placer.isSneaking()) {
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                BlockPos neighborPos = pos.offset(direction);
                BlockState neighborState = world.getBlockState(neighborPos);

                if (neighborState.getBlock() instanceof HallnoxBulbBlock && state.get(FACING_PROPERTIES.get(direction))) {
                    world.setBlockState(neighborPos, neighborState.with(FACING_PROPERTIES.get(direction.getOpposite()), true));
                }
            }
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState newState = super.getPlacementState(ctx);

        // silence, intellij warnings
        if (newState != null) {
            World world = ctx.getWorld();
            BlockPos pos = ctx.getBlockPos();
            Direction clickedFace = ctx.getSide();
            Direction offsetDir = clickedFace.getOpposite();
            PlayerEntity player = ctx.getPlayer();

            newState = newState.with(WATERLOGGED, world.getFluidState(pos).isOf(Fluids.WATER.getStill()));

            // just so we don't have to repeatedly check for this
            if (player == null) return newState;

            // don't autoconnect to anything if holding shift
            BlockState clickedState = world.getBlockState(pos.offset(offsetDir));
            if (!player.isSneaking()) {
                // connect to blocks with solid center
                if (shouldConnect(world, clickedState, pos.offset(clickedFace.getOpposite()), clickedFace.getOpposite())) {
                    newState = newState.with(FACING_PROPERTIES.get(offsetDir), true);
                }

                // automatically connect to other hallnox bulbs - updates them to connect, too
                for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                    BlockPos neighborPos = pos.offset(direction);
                    BlockState neighborState = world.getBlockState(neighborPos);
                    if (neighborState.getBlock() instanceof HallnoxBulbBlock) {
                        newState = newState.with(FACING_PROPERTIES.get(direction), true);
                    }
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
