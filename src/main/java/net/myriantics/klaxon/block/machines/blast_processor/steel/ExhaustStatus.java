package net.myriantics.klaxon.block.machines.blast_processor.steel;

import net.minecraft.util.StringRepresentable;

public enum ExhaustStatus implements StringRepresentable {
    CLEAR("clear"),
    OBSTRUCTED("obstructed"),
    IGNITED("ignited");

    public boolean isObstructed() {
        return this != CLEAR;
    }

    private final String serializedName;

    ExhaustStatus(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }
}
