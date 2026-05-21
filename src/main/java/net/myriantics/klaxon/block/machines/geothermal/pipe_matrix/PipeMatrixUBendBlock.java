package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.myriantics.klaxon.block.decor.hallnox_bulb.NeighborPlacementListener;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PipeMatrixUBendBlock extends Block implements Wrenchable, PipeMatrix, NeighborPlacementListener {

    public static final EnumProperty<UBendRotation> ROTATION = KlaxonBlockStateProperties.U_BEND_ROTATION;
    // Tracks whether this pipe matrix loop is part of a valid structure or not.
    public static final BooleanProperty FORMED = KlaxonBlockStateProperties.FORMED;

    protected static final WrenchInteraction DISCONNECT_INTERACTION = WrenchInteraction.of(KlaxonWrenchActionTypes.DISCONNECT, (context, rotation) -> handleDisconnect(context));

    private PipeMatrixSegmentBlock segmentBlock;

    public PipeMatrixUBendBlock(Properties settings) {
        super(settings);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(ROTATION, UBendRotation.UP_BENT_AROUND_Z)
                .setValue(FORMED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROTATION, FORMED);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return neighborState instanceof PipeMatrix && neighborState.getValue(FORMED) ? state : state.setValue(FORMED, false);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, state.getValue(ROTATION).rotate(rotation));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction clickedDirection = ctx.getClickedFace();
        Direction facingDirection = ctx.getNearestLookingDirection();

        // try facing direction first
        @Nullable UBendRotation rotation = UBendRotation.from(clickedDirection, facingDirection.getAxis());
        if (rotation == null) {
            // try horizontal facing direction next
            rotation = UBendRotation.from(clickedDirection, ctx.getHorizontalDirection().getAxis());
        }
        // if all else fails, just grab the first valid rotation for the given direction
        return this.defaultBlockState().setValue(ROTATION, rotation == null ? UBendRotation.firstMatchingDirection(facingDirection) : rotation);
    }

    @Override
    public boolean sideHasExposedPipes(BlockState state, Direction direction) {
        return direction.equals(state.getValue(ROTATION).getExposedFace());
    }

    @Override
    public Item asItem() {
        return this.getSegmentBlock().asItem();
    }

    public PipeMatrixSegmentBlock getSegmentBlock() {
        if (this.segmentBlock == null) {
            this.segmentBlock = PipeMatrixSegmentBlock.LOOP_TO_MATRIX.get(this);
        }

        return this.segmentBlock;
    }

    @Override
    public void onAdjacentPlaceOnSide(Level world, BlockPos pos, BlockState state, BlockPos placedPos, BlockState placedState, BlockPlaceContext context) {
        // if the adjacent placed block would extend this one, replace this block with a pipe matrix
        if (placedState.getBlock() instanceof PipeMatrixUBendBlock && placedState.getValue(ROTATION).getExposedFace().equals(context.getClickedFace())) {
            world.setBlockAndUpdate(pos, getSegmentBlock().defaultBlockState().setValue(PipeMatrixSegmentBlock.AXIS, context.getClickedFace().getAxis()));
        }
    }

    private static Optional<InteractionResult> handleDisconnect(WrenchActionContext context) {
        Level level = context.level();
        BlockState state = context.getTargetState();
        BlockPos pos = context.getTargetPos();

        if (!(state.getBlock() instanceof PipeMatrixUBendBlock uBendBlock)) {
            throw new AssertionError();
        }

        @Nullable PipeMatrixSegmentBlock segmentBlock = uBendBlock.getSegmentBlock();

        if (segmentBlock != null) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, segmentBlock.defaultBlockState().setValue(PipeMatrixSegmentBlock.AXIS, state.getValue(ROTATION).getExposedFace().getAxis()));
            }
            level.playSound(
                    context instanceof WrenchActionContext.Manual manual ? manual.getPlayer() : null,
                    pos,
                    uBendBlock.soundType.getPlaceSound(),
                    SoundSource.BLOCKS,
                    0.8f + (0.2f * level.getRandom().nextFloat()),
                    0.4f + (0.4f * level.getRandom().nextFloat())
            );
            switch (context) {
                case WrenchActionContext.Manual manual -> level.gameEvent(manual.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
                case WrenchActionContext.Dispenser dispenser -> level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
            }

            return Optional.of(InteractionResult.SUCCESS);
        } else {
            return Optional.of(InteractionResult.FAIL);
        }
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return DISCONNECT_INTERACTION.toSingletonMap();
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return DISCONNECT_INTERACTION;
    }
}
