package net.myriantics.klaxon.block.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.Fluids;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;

import java.util.Optional;

public class SteelTrapdoorBlock extends TrapDoorBlock implements Wrenchable {

    protected static final WrenchInteraction FLIP = WrenchInteraction.of(KlaxonWrenchActionTypes.FLIP, SteelTrapdoorBlock::handleFlipAction);

    public SteelTrapdoorBlock(BlockSetType type, Properties settings) {
        super(type, settings);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClientSide()) {
            boolean isRecievingPower = world.hasNeighborSignal(pos);

            if (isRecievingPower != state.getValue(POWERED)) {
                BlockState newState = state.cycle(POWERED);

                // flips on up signal
                if (isRecievingPower) {
                    this.playSound(null, world, pos, !state.getValue(OPEN));
                    newState = newState.cycle(OPEN);

                    if (state.getValue(WATERLOGGED)) {
                        world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
                    }
                }

                world.setBlockAndUpdate(pos, newState);
            }
        }
    }

    private static Optional<InteractionResult> handleFlipAction(WrenchActionContext context) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        if (!(state.getBlock() instanceof SteelTrapdoorBlock trapdoor)) {
            throw new AssertionError();
        }

        trapdoor.playSound(
                context instanceof WrenchActionContext.Manual manual ? manual.getPlayer() : null,
                level,
                pos,
                !state.getValue(OPEN)
        );

        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, state.cycle(OPEN));
        }

        return Optional.of(InteractionResult.SUCCESS);
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
