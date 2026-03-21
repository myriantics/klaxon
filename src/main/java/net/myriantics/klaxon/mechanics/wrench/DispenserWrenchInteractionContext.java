package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public record DispenserWrenchInteractionContext(BlockState targetState, BlockPos targetPos, ItemStack stack, ServerLevel serverWorld, Direction dispenserFacing, BlockSource pointer) {
}
