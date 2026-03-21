package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public record ManualWrenchInteractionContext(BlockState targetState, ItemStack stack, Level world, Player player, InteractionHand hand, BlockHitResult hitResult) {
}
