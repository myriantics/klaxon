package net.myriantics.klaxon.mechanics.wrench.interaction;

import net.minecraft.world.InteractionResult;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;

import java.util.Optional;

public interface WrenchActionHandler {
    Optional<InteractionResult> handle(WrenchActionContext context);
}
