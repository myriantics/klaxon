package net.myriantics.klaxon.api;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface Wrenchable {
    ItemActionResult onWrenched(BlockState targetState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult);
}
