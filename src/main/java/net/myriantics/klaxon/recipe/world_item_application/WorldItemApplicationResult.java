package net.myriantics.klaxon.recipe.world_item_application;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface WorldItemApplicationResult {
    Optional<BlockState> getResultState(Level world, BlockState state, BlockPos pos, Direction clickDirection, @Nullable Player lookDirection);
}
