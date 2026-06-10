package net.myriantics.klaxon.mechanics.logistics.itemduct;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface DuctNode {

    boolean push(DuctPayload payload, Direction inputFace, int remainingDepth);

    void setPayload(@Nullable DuctPayload payload);

    @Nullable DuctPayload getPayload();

    String getStatusForDirection(Direction direction);
}
