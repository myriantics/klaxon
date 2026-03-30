package net.myriantics.klaxon.mechanics.wrench.interaction;

import net.minecraft.world.InteractionResult;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.util.BlockFaceRegion;

import java.util.Optional;

public interface WrenchActionHandler {
    Optional<InteractionResult> handle(WrenchActionContext context, BlockFaceRegion.Rotation rotation);
}
