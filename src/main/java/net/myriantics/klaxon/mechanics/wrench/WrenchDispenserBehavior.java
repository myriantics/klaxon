package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.util.BlockFaceRegion;

import java.util.Optional;

public class WrenchDispenserBehavior extends OptionalDispenseItemBehavior {
    private boolean shouldPlayEffects = true;

    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        ServerLevel serverLevel = source.level();
        Direction facing = source.state().getValue(DispenserBlock.FACING);
        BlockPos targetPos = source.pos().relative(facing);
        BlockState targetState = serverLevel.getBlockState(targetPos);

        setSuccess(false);
        shouldPlayEffects = true;

        // cancel wrench interaction if a predicate blocks it
        if (WrenchInteractionDenialPredicate.wrenchInteractionBlocked(serverLevel.getServer().reloadableRegistries().get(), targetState)) {
            return stack;
        }

        WrenchActionContext.Dispenser context = new WrenchActionContext.Dispenser(serverLevel, targetState, targetPos, stack, facing, source);

        // run custom behavior if present
        if (targetState.getBlock() instanceof Wrenchable wrenchable) {
            WrenchInteraction type = wrenchable.getDispenserInteraction(context);
            Optional<InteractionResult> result = type.handle(context, BlockFaceRegion.Rotation.R0);

            // we don't need to set blockstate here because it's done in the above method
            if (result.isPresent()) {
                serverLevel.updateNeighbourForOutputSignal(source.pos(), source.state().getBlock());
                setSuccess(true);
                return stack;
            }
        }

        // apply all valid behaviors to the new state
        for (BlockStateWrenchBehavior<? extends Comparable<?>> behavior : KlaxonRegistries.BLOCK_STATE_WRENCH_BEHAVIORS) {
            WrenchInteraction interaction = behavior.getDispenserInteraction(context);
            Optional<InteractionResult> result = interaction.handle(context, BlockFaceRegion.Rotation.R0);
            if (result.isPresent()) {
                serverLevel.updateNeighbourForOutputSignal(source.pos(), source.state().getBlock());
                setSuccess(true);
                return stack;
            }
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
