package net.myriantics.klaxon.block.decor;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import org.jetbrains.annotations.Nullable;

public class SteelDoorBlock extends DoorBlock implements Wrenchable {
    public SteelDoorBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient()) {
            boolean isRecievingPower = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));

            if (isRecievingPower != state.get(POWERED)) {
                BlockState newState = state.cycle(POWERED);

                // flips on up signal
                if (!this.getDefaultState().isOf(sourceBlock) && isRecievingPower) {
                    // dude these random ass private methods have me tweaking
                    playOpenCloseSound(null, world, pos, !state.get(OPEN));
                    newState = newState.cycle(OPEN);
                }

                world.setBlockState(pos, newState);
            }
        }
    }

    private void playOpenCloseSound(@Nullable Entity entity, World world, BlockPos pos, boolean open) {
        world.playSound(
                entity, pos, open ? this.getBlockSetType().doorOpen() : this.getBlockSetType().doorClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F
        );
    }

    @Override
    public ItemActionResult onManualWrenchInteraction(ManualWrenchInteractionContext context) {
        BlockPos targetPos = context.hitResult().getBlockPos();

        this.playOpenCloseSound(context.player(), context.world(), targetPos, !context.targetState().get(OPEN));
        context.world().setBlockState(targetPos, context.targetState().cycle(OPEN));

        return ItemActionResult.SUCCESS;
    }

    @Override
    public boolean onDispenserWrenchInteraction(DispenserWrenchInteractionContext context) {
        this.playOpenCloseSound(null, context.serverWorld(), context.targetPos(), !context.targetState().get(OPEN));
        context.serverWorld().setBlockState(context.targetPos(), context.targetState().cycle(OPEN));

        return true;
    }
}
