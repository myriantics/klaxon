package net.myriantics.klaxon.mechanics.logistics.itemduct;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IDuctNodeBlock {

    boolean canConnectionOpen(BlockState state, Direction face);

    boolean isConnectionOpen(BlockState state, Direction face);

    BlockState setConnectionForFace(BlockState original, Direction face, boolean connected);
}
