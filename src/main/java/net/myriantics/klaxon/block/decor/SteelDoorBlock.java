package net.myriantics.klaxon.block.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;

public class SteelDoorBlock extends DoorBlock implements Wrenchable {
    public SteelDoorBlock(BlockSetType type, Properties settings) {
        super(type, settings);
    }

    protected static final WrenchInteraction FLIP = WrenchInteraction.of(KlaxonWrenchActionTypes.FLIP, (context, rotation) -> java.util.Optional.ofNullable(handleFlipAction(context)));

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!level.isClientSide()) {
            boolean isRecievingPower = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));

            if (isRecievingPower != state.getValue(POWERED)) {
                BlockState newState = state.cycle(POWERED);

                // flips on up signal
                if (!this.defaultBlockState().is(sourceBlock) && isRecievingPower) {
                    boolean isOpening = !state.getValue(OPEN);
                    this.playSound(null, level, pos, isOpening);
                    Holder<GameEvent> gameEvent = isOpening ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE;
                    level.gameEvent(null, gameEvent, pos);
                    newState = newState.cycle(OPEN);
                }

                level.setBlockAndUpdate(pos, newState);
            }
        }
    }

    private static InteractionResult handleFlipAction(WrenchActionContext context) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();

        boolean isOpening = !state.getValue(OPEN);

        if (!(state.getBlock() instanceof SteelDoorBlock steelDoorBlock)) {
            throw new AssertionError();
        }

        steelDoorBlock.playSound(
                context instanceof WrenchActionContext.Manual manual ? manual.getPlayer() : null,
                level,
                pos,
                isOpening
        );

        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, state.cycle(OPEN));
            level.gameEvent(
                    context instanceof WrenchActionContext.Manual manual ? manual.getPlayer() : null,
                    isOpening ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE,
                    pos
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return FLIP.toSingletonMap();
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return FLIP;
    }
}
