package net.myriantics.klaxon.mechanics.explosive_catalyst;

import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;

public interface ExplosiveCatalystVessel {
    boolean shouldExposeExplosiveCatalystData();

    ExplosiveCatalystData getRawData();

    ExplosiveCatalystData getEffectiveCatalystData();
}
