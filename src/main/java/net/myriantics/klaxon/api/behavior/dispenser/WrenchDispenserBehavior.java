package net.myriantics.klaxon.api.behavior.dispenser;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public class WrenchDispenserBehavior extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        if (pointer.world() instanceof ServerWorld serverWorld) {
            Direction facing = pointer.state().get(DispenserBlock.FACING);
            BlockPos targetPos = pointer.pos().offset(facing);
            BlockState targetState = serverWorld.getBlockState(targetPos);

            if (!targetState.isIn(KlaxonBlockTags.WRENCH_INTERACTION_DENYLIST) && targetState.isIn(KlaxonBlockTags.WRENCH_ROTATABLE)) {
                ActionResult result = WrenchItem.rotateBlock(serverWorld, targetPos, targetState, facing, null, 0f);
                if (result.isAccepted()) {
                    setSuccess(true);
                }
            }
        }

        return stack;
    }
}
