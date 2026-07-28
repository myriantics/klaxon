package net.myriantics.klaxon.mechanics.explosive_catalyst;

public interface ExplosiveCatalystVessel {

    ExplosiveCatalystData getRawData();

    ExplosiveCatalystData getEffectiveCatalystData();

    default boolean hasDataReady() {
        return true;
    }
}
