package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record DispenserWrenchInteractionContext(BlockState targetState, BlockPos targetPos, ItemStack stack, ServerWorld serverWorld, Direction dispenserFacing, BlockPointer pointer) {
}
