package net.myriantics.klaxon.mechanics.machinery.part.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum StackingStrategy implements StringRepresentable {
    ADD("add"),
    USE_STRONGEST("strongest"),
    USE_FIRST("first");

    public static final Codec<StackingStrategy> CODEC = StringRepresentable.fromEnum(StackingStrategy::values);

    private final String name;

    StackingStrategy(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
