package net.myriantics.klaxon.mechanics.wire_redirector;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface KlaxonRedstoneWireRedirector {
    boolean shouldRedirect(BlockState state, Direction wireOutboundConnectionDirection);
}
