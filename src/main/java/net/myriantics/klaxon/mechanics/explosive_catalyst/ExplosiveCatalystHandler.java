package net.myriantics.klaxon.mechanics.explosive_catalyst;

import net.minecraft.core.Position;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;

public abstract class ExplosiveCatalystHandler {

    public abstract void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld);
}
