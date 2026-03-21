package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public class WrenchDispenserBehavior extends OptionalDispenseItemBehavior {
    private boolean shouldPlayEffects = true;

    @Override
    protected ItemStack execute(BlockSource pointer, ItemStack stack) {
        ServerLevel serverWorld = pointer.level();
        Direction facing = pointer.state().getValue(DispenserBlock.FACING);
        BlockPos targetPos = pointer.pos().relative(facing);
        BlockState targetState = serverWorld.getBlockState(targetPos);

        setSuccess(false);
        shouldPlayEffects = true;

        // cancel wrench interaction if a predicate blocks it
        if (WrenchInteractionDenialPredicate.wrenchInteractionBlocked(serverWorld.getServer().reloadableRegistries().get(), targetState)) {
            return stack;
        }

        DispenserWrenchInteractionContext context = new DispenserWrenchInteractionContext(targetState, targetPos, stack, serverWorld, facing, pointer);

        // run custom behavior if present
        if (targetState.getBlock() instanceof Wrenchable wrenchable) {
            boolean success = wrenchable.onDispenserWrenchInteraction(context);

            // we don't need to set blockstate here because it's done in the above method
            if (success) {
                serverWorld.updateNeighbourForOutputSignal(pointer.pos(), pointer.state().getBlock());
                setSuccess(true);
                return stack;
            }
        }

        BlockState newState = targetState;

        // apply all valid behaviors to the new state
        for (BlockStateWrenchBehavior<? extends Comparable<?>> behavior : KlaxonRegistries.BLOCK_STATE_WRENCH_BEHAVIORS) {
            newState = behavior.applyDispenser(newState, context);
        }

        // only commit changes to the world if we've changed the block state
        if (!newState.equals(targetState)) {
            serverWorld.setBlockAndUpdate(targetPos, newState);
            serverWorld.neighborChanged(targetPos, pointer.state().getBlock(), pointer.pos());
            serverWorld.updateNeighbourForOutputSignal(pointer.pos(), pointer.state().getBlock());
            setSuccess(true);
            shouldPlayEffects = false;
        }

        return stack;
    }

    @Override
    protected void playSound(BlockSource pointer) {
        if (this.shouldPlayEffects) {
            super.playSound(pointer);
        }
    }

    @Override
    protected void playAnimation(BlockSource pointer, Direction side) {
        if (this.shouldPlayEffects) {
            super.playAnimation(pointer, side);
        }
    }
}
