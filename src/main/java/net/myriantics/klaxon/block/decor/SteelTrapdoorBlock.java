package net.myriantics.klaxon.block.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.Fluids;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;

public class SteelTrapdoorBlock extends TrapDoorBlock implements Wrenchable {
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

    @Override
    public InteractionResult onManualWrenchInteraction(ManualWrenchInteractionContext context) {
        BlockPos targetPos = context.hitResult().getBlockPos();

        this.playSound(context.player(), context.world(), targetPos, !context.targetState().getValue(OPEN));
        context.world().setBlockAndUpdate(targetPos, context.targetState().cycle(OPEN));

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onDispenserWrenchInteraction(DispenserWrenchInteractionContext context) {
        this.playSound(null, context.serverWorld(), context.targetPos(), !context.targetState().getValue(OPEN));
        context.serverWorld().setBlockAndUpdate(context.targetPos(), context.targetState().cycle(OPEN));

        return true;
    }
}
