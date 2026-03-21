package net.myriantics.klaxon.block.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import org.jetbrains.annotations.Nullable;

public class SteelDoorBlock extends DoorBlock implements Wrenchable {
    public SteelDoorBlock(BlockSetType type, Properties settings) {
        super(type, settings);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClientSide()) {
            boolean isRecievingPower = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));

            if (isRecievingPower != state.getValue(POWERED)) {
                BlockState newState = state.cycle(POWERED);

                // flips on up signal
                if (!this.defaultBlockState().is(sourceBlock) && isRecievingPower) {
                    // dude these random ass private methods have me tweaking
                    playSound(null, world, pos, !state.getValue(OPEN));
                    newState = newState.cycle(OPEN);
                }

                world.setBlockAndUpdate(pos, newState);
            }
        }
    }

    private void playSound(@Nullable Entity entity, Level world, BlockPos pos, boolean open) {
        world.playSound(
                entity, pos, open ? this.type().doorOpen() : this.type().doorClose(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F
        );
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
