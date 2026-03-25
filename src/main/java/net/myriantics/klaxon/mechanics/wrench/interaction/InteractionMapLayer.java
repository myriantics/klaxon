package net.myriantics.klaxon.mechanics.wrench.interaction;

import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

public interface InteractionMapLayer {
    @Nullable WrenchInteraction getInteraction(Vec2 faceClickedPos);
}
