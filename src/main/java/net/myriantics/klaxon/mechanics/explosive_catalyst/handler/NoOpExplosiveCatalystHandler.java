package net.myriantics.klaxon.mechanics.explosive_catalyst.handler;

import net.minecraft.core.Position;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystHandler;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;

public class NoOpExplosiveCatalystHandler extends ExplosiveCatalystHandler {
    @Override
    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
    }
}
