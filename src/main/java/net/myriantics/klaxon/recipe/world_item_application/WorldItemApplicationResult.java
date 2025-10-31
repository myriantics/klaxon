package net.myriantics.klaxon.recipe.world_item_application;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface WorldItemApplicationResult {
    Optional<BlockState> getResultState(World world, BlockState state, BlockPos pos, Direction clickDirection, @Nullable PlayerEntity lookDirection);
}
