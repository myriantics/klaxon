package net.myriantics.klaxon.mechanics.wrench;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum WrenchUsageType implements StringRepresentable {
    ROTATION,
    PICKUP;

    public static Codec<WrenchUsageType> CODEC = StringRepresentable.fromEnum(WrenchUsageType::values);

    @Override
    public String getSerializedName() {
        return switch (this) {
            case ROTATION -> "rotation";
            case PICKUP -> "pickup";
        };
    }
}
