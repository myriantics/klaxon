package net.myriantics.klaxon.mechanics.logistics.itemduct;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface DuctNode {

    boolean push(DuctPayload payload, Direction inputFace, int remainingDepth);

    void setPayload(@Nullable DuctPayload payload);

    @Nullable DuctPayload getPayload();

    byte getOpenDirections();

    default boolean isDirectionOpen(Direction direction) {
        return (this.getOpenDirections() & (0x01 << direction.ordinal())) != 0;
    }

    String getStatusForDirection(Direction direction);
}
