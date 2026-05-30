package net.myriantics.klaxon.mechanics.logistics.itemduct;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface DuctNode {
    @Nullable Direction findNextDirection(Direction movementDirection);

    boolean push(Direction inputDirection, int depth);

    void setPayload(@Nullable DuctPayload payload);
}
