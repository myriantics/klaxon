package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public class WrenchDispenserBehavior extends FallibleItemDispenserBehavior {
    private boolean shouldPlayEffects = true;

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld serverWorld = pointer.world();
        Direction facing = pointer.state().get(DispenserBlock.FACING);
        BlockPos targetPos = pointer.pos().offset(facing);
        BlockState targetState = serverWorld.getBlockState(targetPos);

        setSuccess(false);
        shouldPlayEffects = true;

        // cancel wrench interaction if a predicate blocks it
        if (WrenchInteractionDenialPredicate.wrenchInteractionBlocked(serverWorld.getServer().getReloadableRegistries().getRegistryManager(), targetState)) {
            return stack;
        }

        DispenserWrenchInteractionContext context = new DispenserWrenchInteractionContext(targetState, targetPos, stack, serverWorld, facing, pointer);

        // run custom behavior if present
        if (targetState.getBlock() instanceof Wrenchable wrenchable) {
            boolean success = wrenchable.onDispenserWrenchInteraction(context);

            // we don't need to set blockstate here because it's done in the above method
            if (success) {
                serverWorld.updateComparators(pointer.pos(), pointer.state().getBlock());
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
            serverWorld.setBlockState(targetPos, newState);
            serverWorld.updateNeighbor(targetPos, pointer.state().getBlock(), pointer.pos());
            serverWorld.updateComparators(pointer.pos(), pointer.state().getBlock());
            setSuccess(true);
            shouldPlayEffects = false;
        }

        return stack;
    }

    @Override
    protected void playSound(BlockPointer pointer) {
        if (this.shouldPlayEffects) {
            super.playSound(pointer);
        }
    }

    @Override
    protected void spawnParticles(BlockPointer pointer, Direction side) {
        if (this.shouldPlayEffects) {
            super.spawnParticles(pointer, side);
        }
    }
}
