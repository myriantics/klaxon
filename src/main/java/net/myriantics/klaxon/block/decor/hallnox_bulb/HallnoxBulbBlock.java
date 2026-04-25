package net.myriantics.klaxon.block.decor.hallnox_bulb;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
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
import net.myriantics.klaxon.mechanics.wrench.WrenchActionType;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class HallnoxBulbBlock extends PipeBlock implements SimpleWaterloggedBlock, Wrenchable, NeighborPlacementListener {

    public static final MapCodec<HallnoxBulbBlock> CODEC = simpleCodec(HallnoxBulbBlock::new);

    private static final float RADIUS = 0.3125f; // 5/16 pixels
    private static final VoxelShape BASE_SHAPE = Block.box(2, 2, 2, 14, 14, 14);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final DirectionalWrenchInteraction EXTRUDE = DirectionalWrenchInteraction.of(KlaxonWrenchActionTypes.EXTRUDE, HallnoxBulbBlock::handleExtrude);
    protected static final DirectionalWrenchInteraction RETRACT = DirectionalWrenchInteraction.of(KlaxonWrenchActionTypes.RETRACT, HallnoxBulbBlock::handleRetract);
    protected static final DirectionalWrenchInteraction CONNECT = DirectionalWrenchInteraction.of(KlaxonWrenchActionTypes.CONNECT, HallnoxBulbBlock::handleConnect);
    protected static final DirectionalWrenchInteraction DISCONNECT = DirectionalWrenchInteraction.of(KlaxonWrenchActionTypes.DISCONNECT, HallnoxBulbBlock::handleDisconnect);

    private static Optional<InteractionResult> handleDisconnect(WrenchActionContext context, Direction direction) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();

        BlockPos conjoiningPos = pos.relative(direction);
        BlockState conjoiningState = level.getBlockState(conjoiningPos);

        // just make sure its got the property this time - we check for whether its extruded or not before calling this.
        BooleanProperty oppositeProperty = PROPERTY_BY_DIRECTION.get(direction.getOpposite());
        if (conjoiningState.getBlock() instanceof HallnoxBulbBlock && conjoiningState.hasProperty(oppositeProperty)) {
            level.setBlockAndUpdate(conjoiningPos, conjoiningState.setValue(oppositeProperty, true));
        }

        // we know we can set our own state
        level.setBlockAndUpdate(pos, state.setValue(PROPERTY_BY_DIRECTION.get(direction), true));
        playToggleSound(level, context, false);

        return Optional.of(InteractionResult.SUCCESS);
    }

    private static Optional<InteractionResult> handleConnect(WrenchActionContext context, Direction direction) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();

        BlockPos conjoiningPos = pos.relative(direction);
        BlockState conjoiningState = level.getBlockState(conjoiningPos);

        // only change the other state if we're sure it's not extruded
        BooleanProperty oppositeProperty = PROPERTY_BY_DIRECTION.get(direction.getOpposite());
        if (conjoiningState.getBlock() instanceof HallnoxBulbBlock && conjoiningState.hasProperty(oppositeProperty) && !conjoiningState.getValue(oppositeProperty)) {
            level.setBlockAndUpdate(conjoiningPos, conjoiningState.setValue(oppositeProperty, true));
        }

        // we know we can set our own state
        level.setBlockAndUpdate(pos, state.setValue(PROPERTY_BY_DIRECTION.get(direction), true));
        playToggleSound(level, context, false);

        return Optional.of(InteractionResult.SUCCESS);
    }

    private static Optional<InteractionResult> handleRetract(WrenchActionContext context, Direction direction) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();

        level.setBlockAndUpdate(pos, state.setValue(PROPERTY_BY_DIRECTION.get(direction), false));
        playToggleSound(level, context, false);

        return Optional.of(InteractionResult.SUCCESS);
    }

    private static Optional<InteractionResult> handleExtrude(WrenchActionContext context, Direction direction) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();

        level.setBlockAndUpdate(pos, state.setValue(PROPERTY_BY_DIRECTION.get(direction), true));
        playToggleSound(level, context, true);

        return Optional.of(InteractionResult.SUCCESS);
    }

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

    private static void playToggleSound(Level level, WrenchActionContext context, boolean extrude) {
        BlockPos pos = context.getTargetPos();
        SoundType soundGroup = KlaxonBlocks.STEEL_PLATING_BLOCK.value().defaultBlockState().getSoundType();
        level.playSound(
                context instanceof WrenchActionContext.Manual manual ? manual.getPlayer() : null,
                pos,
                extrude ? soundGroup.getPlaceSound() : soundGroup.getBreakSound(),
                SoundSource.BLOCKS,
                0.6f + (0.2f * level.getRandom().nextFloat()),
                0.2f + (0.4f * level.getRandom().nextFloat())
        );

        // proc sculk sensors
        switch (context) {
            case WrenchActionContext.Manual manual -> level.gameEvent(manual.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
            case WrenchActionContext.Dispenser dispenser -> level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(context.getTargetState()));
        }
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

    protected static Direction selectFaceFromContext(WrenchActionContext context) {
        return switch (context) {
            case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing().getOpposite();
            case WrenchActionContext.Manual manual -> {
                Vec3 hitPos = manual.getHitResult().getLocation().subtract(Vec3.atCenterOf(context.getTargetPos()));
                yield Direction.getNearest(hitPos.x(), hitPos.y(), hitPos.z());
            }
        };
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        Level level = context.level();
        BlockState state = context.getTargetState();
        BlockPos pos = context.getTargetPos();

        Direction selectedFace = selectFaceFromContext(context);

        BlockPos conjoiningPos = pos.relative(selectedFace);
        BlockState conjoiningState = level.getBlockState(conjoiningPos);

        boolean extruded = state.getValue(PROPERTY_BY_DIRECTION.get(selectedFace));

        BooleanProperty opposingDirection = PROPERTY_BY_DIRECTION.get(selectedFace.getOpposite());
        if (conjoiningState.getBlock() instanceof HallnoxBulbBlock && conjoiningState.hasProperty(opposingDirection)) {
            boolean conjoiningExtruded = conjoiningState.getValue(opposingDirection);
            if (extruded && conjoiningExtruded) { // if both are extruded, perform disconnect
                return DISCONNECT.forDirection(selectedFace).toSingletonMap();
            } else if (conjoiningExtruded || !extruded) { // if conjoining is extruded or origin isn't, perform connect
                return CONNECT.forDirection(selectedFace).toSingletonMap();
            }
        }

        // conjoining special cases have been accounted for, so do basic behavior
        return extruded
                ? RETRACT.forDirection(selectedFace).toSingletonMap()
                : EXTRUDE.forDirection(selectedFace).toSingletonMap();
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        Direction selectedFace = context.getDispenserFacing().getOpposite();
        return context.getTargetState().getValue(PROPERTY_BY_DIRECTION.get(selectedFace))
                ? RETRACT.forDirection(selectedFace)
                : EXTRUDE.forDirection(selectedFace);
    }

    protected static class DirectionalWrenchInteraction {
        private final Map<Direction, WrenchInteraction> direction2Interaction;

        protected DirectionalWrenchInteraction(WrenchActionType type, DirectionalWrenchActionHandler handler) {
            this.direction2Interaction = Map.of(
                    Direction.DOWN, WrenchInteraction.of(type, ((context, rotation) -> handler.handle(context, Direction.DOWN))),
                    Direction.UP, WrenchInteraction.of(type, ((context, rotation) -> handler.handle(context, Direction.UP))),
                    Direction.NORTH, WrenchInteraction.of(type, ((context, rotation) -> handler.handle(context, Direction.NORTH))),
                    Direction.SOUTH, WrenchInteraction.of(type, ((context, rotation) -> handler.handle(context, Direction.SOUTH))),
                    Direction.EAST, WrenchInteraction.of(type, ((context, rotation) -> handler.handle(context, Direction.EAST))),
                    Direction.WEST, WrenchInteraction.of(type, ((context, rotation) -> handler.handle(context, Direction.WEST)))
            );
        }

        public static DirectionalWrenchInteraction of(Holder<WrenchActionType> typeHolder, DirectionalWrenchActionHandler handler) {
            return of(typeHolder.value(), handler);
        }

        public static DirectionalWrenchInteraction of(WrenchActionType type, DirectionalWrenchActionHandler handler) {
            return new DirectionalWrenchInteraction(type, handler);
        }

        public WrenchInteraction forDirection(Direction direction) {
            return this.direction2Interaction.get(direction);
        }

    }

    protected interface DirectionalWrenchActionHandler {
        Optional<InteractionResult> handle(WrenchActionContext context, Direction direction);
    }
}
