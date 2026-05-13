package net.myriantics.klaxon.mechanics.explosive_catalyst;

public interface ExplosiveCatalystVessel {
    boolean shouldExposeExplosiveCatalystData();

    ExplosiveCatalystData getRawData();

    ExplosiveCatalystData getEffectiveCatalystData();
}
