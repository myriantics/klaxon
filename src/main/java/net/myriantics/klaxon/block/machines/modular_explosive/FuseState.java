package net.myriantics.klaxon.block.machines.modular_explosive;

import net.minecraft.util.StringRepresentable;

public enum FuseState implements StringRepresentable {
    INERT,
    FAR,
    CLOSE,
    IMMINENT;

    public FuseState of(int fuseTime, int maxFuseTime) {
        if (fuseTime == -1 || maxFuseTime == 0) {
            return INERT;
        }

        return INERT;
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case INERT -> "inert";
            case FAR -> "far";
            case CLOSE -> "close";
            case IMMINENT -> "imminent";
        };
    }
}
