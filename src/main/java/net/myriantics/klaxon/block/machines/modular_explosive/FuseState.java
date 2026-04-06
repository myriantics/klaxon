package net.myriantics.klaxon.block.machines.modular_explosive;

import net.minecraft.util.StringRepresentable;

public enum FuseState implements StringRepresentable {
    INERT,
    FAR,
    CLOSE,
    IMMINENT;

    public boolean isCountingDown() {
        return !this.equals(INERT);
    }

    public static FuseState of(int fuseTime, int maxFuseTime) {
        if (fuseTime == -1 || maxFuseTime == 0) {
            return INERT;
        }

        if (fuseTime < 20 || fuseTime < maxFuseTime * 0.2) {
            return IMMINENT;
        }

        if (fuseTime < 40 || fuseTime < maxFuseTime * 0.6) {
            return CLOSE;
        }

        return FAR;
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
