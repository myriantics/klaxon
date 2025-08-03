package net.myriantics.klaxon.api.behavior.dispenser;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.api.Wrenchable;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

import java.util.Optional;

public class WrenchDispenserBehavior extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld serverWorld = pointer.world();
        Direction facing = pointer.state().get(DispenserBlock.FACING);
        BlockPos targetPos = pointer.pos().offset(facing);
        BlockState targetState = serverWorld.getBlockState(targetPos);

        if (!targetState.isIn(KlaxonBlockTags.WRENCH_ROTATION_DENYLIST)) {
            // run custom behavior if present
            if (targetState.getBlock() instanceof Wrenchable wrenchable) {
                ItemActionResult result = wrenchable.onDispenserWrenched(targetState, targetPos, stack, serverWorld, facing, pointer);

                // we don't need to set blockstate here because it's done in the above method
                if (result.isAccepted()) {
                    serverWorld.updateComparators(pointer.pos(), pointer.state().getBlock());
                    setSuccess(true);
                    return stack;
                }
            }

            // run default behavior if present
            if (targetState.isIn(KlaxonBlockTags.WRENCH_ROTATION_ALLOWLIST)) { // run klaxon's default wrench behavior
                Optional<BlockState> rotatedState = WrenchItem.getRotatedState(serverWorld, targetPos, targetState, facing, null, null);
                if (rotatedState.isPresent()) {
                    serverWorld.setBlockState(targetPos, rotatedState.get());
                    serverWorld.updateNeighbor(targetPos, pointer.state().getBlock(), pointer.pos());
                    serverWorld.updateComparators(pointer.pos(), pointer.state().getBlock());
                    setSuccess(true);
                }
            }
        }

        return stack;
    }
}
