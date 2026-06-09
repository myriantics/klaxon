package net.myriantics.klaxon.mechanics.logistics.itemduct;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IDuctNodeBlock {

    boolean canConnectionOpen(BlockState state, Direction face);

    boolean isConnectionOpen(BlockState state, Direction face);

    BlockState setConnectionForFace(BlockState original, Direction face, boolean connected);

    DuctNode getNode(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity);
}
