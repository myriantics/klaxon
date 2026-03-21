package net.myriantics.klaxon.block.decor.hallnox_bulb;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import org.jetbrains.annotations.Nullable;

public class HallnoxBulbBlock extends PipeBlock implements SimpleWaterloggedBlock, Wrenchable, NeighborPlacementListener {

    public static final MapCodec<HallnoxBulbBlock> CODEC = simpleCodec(HallnoxBulbBlock::new);

    private static final float RADIUS = 0.3125f; // 5/16 pixels
    private static final VoxelShape BASE_SHAPE = Block.box(2, 2, 2, 14, 14, 14);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final VoxelShape[] facingsToFusedShape = new VoxelShape[64];

    public HallnoxBulbBlock(Properties settings) {
        super(RADIUS, settings);
        this.registerDefaultState(defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
        );
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int mask = getAABBIndex(state);

        if (facingsToFusedShape[mask] == null) {
            facingsToFusedShape[mask] = Shapes.or(BASE_SHAPE, shapeByIndex[mask]);
        }

        return facingsToFusedShape[mask];
    }

    protected boolean shouldConnect(Level world, BlockState targetState, BlockPos targetPos, Direction offsetDir) {
        return targetState.isFaceSturdy(world, targetPos, offsetDir.getOpposite(), SupportType.CENTER) || targetState.getBlock() instanceof HallnoxBulbBlock;
    }

    @Override
    public void onAdjacentPlaceOnSide(Level world, BlockPos clickedPos, BlockState clickedState, BlockPos placedPos, BlockState placedState, BlockPlaceContext context) {
        if (context.getPlayer() == null || context.getPlayer().isShiftKeyDown()) {
            return;
        }

        if (shouldConnect(world, placedState, placedPos, context.getClickedFace())) {
            world.setBlockAndUpdate(clickedPos, clickedState.setValue(PROPERTY_BY_DIRECTION.get(context.getClickedFace()), true));
        }
    }


    public void onAdjacentPlaceOnSideWhileNotCrouching(Level world, BlockPos bulbPos, BlockState bulbState, BlockPos placedPos, BlockState placedState, Direction clickedSide) {
        if (shouldConnect(world, placedState, placedPos, clickedSide)) {
            world.setBlockAndUpdate(bulbPos, bulbState.setValue(PROPERTY_BY_DIRECTION.get(clickedSide), true));
        }
    }

    @Override
    public InteractionResult onManualWrenchInteraction(ManualWrenchInteractionContext context) {
        BlockPos targetPos = context.hitResult().getBlockPos();

        Level world = context.world();
        Vec3 hitPos = context.hitResult().getLocation().subtract(Vec3.atCenterOf(targetPos));
        Direction togglingDirection = Direction.getNearest(hitPos.x(), hitPos.y(), hitPos.z());

        BlockPos conjoiningPos = targetPos.relative(togglingDirection);
        BlockState conjoiningState = world.getBlockState(conjoiningPos);

        BooleanProperty toggledProperty = PROPERTY_BY_DIRECTION.get(togglingDirection);
        BooleanProperty conjoiningToggledProperty = PROPERTY_BY_DIRECTION.get(togglingDirection.getOpposite());

        // don't update block states on the client
        if (!world.isClientSide()) {
            // cycle conjoining bulb connector state if possible
            if (
                // make sure conjoining state is also a hallnox bulb
                    conjoiningState.getBlock() instanceof HallnoxBulbBlock
                            // make sure the states match
                            && conjoiningState.getValue(conjoiningToggledProperty)
                            .equals(context.targetState().getValue(toggledProperty)
                            )
            ) {
                world.setBlockAndUpdate(
                        conjoiningPos,
                        conjoiningState.cycle(conjoiningToggledProperty)
                );
            }

            // cycle bulb connector state
            world.setBlockAndUpdate(
                    targetPos,
                    context.targetState().cycle(toggledProperty)
            );
        }


        SoundType soundGroup = KlaxonBlocks.STEEL_PLATING_BLOCK.defaultBlockState().getSoundType();

        world.playSound(
                context.player(),
                targetPos,
                context.targetState().getValue(toggledProperty) ? soundGroup.getBreakSound() : soundGroup.getPlaceSound(),
                SoundSource.BLOCKS,
                0.6f + (0.2f * world.getRandom().nextFloat()),
                0.2f + (0.4f * world.getRandom().nextFloat())
        );

        // this is a stub implementation in ClientWorld so it's fine
        // trip sculk sensors because it's funny
        world.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(context.player(), context.targetState()));

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onDispenserWrenchInteraction(DispenserWrenchInteractionContext context) {
        BooleanProperty toggledProperty = PROPERTY_BY_DIRECTION.get(context.dispenserFacing().getOpposite());
        ServerLevel serverWorld = context.serverWorld();

        // calculations are simpler here because dispensers can only face 6 ways - also can't click on random parts of the block
        serverWorld.setBlockAndUpdate(context.targetPos(), context.targetState().cycle(toggledProperty));

        SoundType soundGroup = KlaxonBlocks.STEEL_PLATING_BLOCK.defaultBlockState().getSoundType();
        serverWorld.playSound(
                null,
                context.targetPos(),
                context.targetState().getValue(toggledProperty) ? soundGroup.getBreakSound() : soundGroup.getPlaceSound(),
                SoundSource.BLOCKS,
                0.6f + (0.2f * serverWorld.getRandom().nextFloat()),
                0.2f + (0.4f * serverWorld.getRandom().nextFloat())
        );

        // proc sculk sensors
        serverWorld.gameEvent(GameEvent.BLOCK_CHANGE, context.targetPos(), GameEvent.Context.of(context.targetState()));

        return true;
    }



    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        // only update neighbors on placement if placement was actually successful
        for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof HallnoxBulbBlock && state.getValue(PROPERTY_BY_DIRECTION.get(direction))) {
                world.setBlockAndUpdate(neighborPos, neighborState.setValue(PROPERTY_BY_DIRECTION.get(direction.getOpposite()), true));
            }
        }

        super.setPlacedBy(world, pos, state, placer, itemStack);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState newState = super.getStateForPlacement(ctx);

        // silence, intellij warnings
        if (newState != null) {
            Level world = ctx.getLevel();
            BlockPos pos = ctx.getClickedPos();
            Direction clickedFace = ctx.getClickedFace();
            Direction offsetDir = clickedFace.getOpposite();
            Player player = ctx.getPlayer();

            newState = newState.setValue(WATERLOGGED, world.getFluidState(pos).is(Fluids.WATER.getSource()));

            // just so we don't have to repeatedly check for this
            if (player == null) return newState;

            // don't autoconnect to anything if holding shift
            BlockState clickedState = world.getBlockState(pos.relative(offsetDir));
            if (player.isShiftKeyDown()) {
                // connect to blocks with solid center
                if (shouldConnect(world, clickedState, pos.relative(clickedFace.getOpposite()), clickedFace.getOpposite())) {
                    newState = newState.setValue(PROPERTY_BY_DIRECTION.get(offsetDir), true);
                }
            } else {
                // automatically connect to other hallnox bulbs - updates them to connect, too
                for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                    BlockPos neighborPos = pos.relative(direction);
                    BlockState neighborState = world.getBlockState(neighborPos);
                    if (shouldConnect(world, neighborState, neighborPos, direction)) {
                        newState = newState.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
                    }
                }
            }
        }

        return newState;
    }

    @Override
    protected MapCodec<HallnoxBulbBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
