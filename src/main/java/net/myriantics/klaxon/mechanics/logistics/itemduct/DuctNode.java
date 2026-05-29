package net.myriantics.klaxon.mechanics.logistics.itemduct;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface DuctNode {
    boolean advance(Direction advancingDirection);

    @Nullable DuctNode last();

    @Nullable DuctNode next();
}
