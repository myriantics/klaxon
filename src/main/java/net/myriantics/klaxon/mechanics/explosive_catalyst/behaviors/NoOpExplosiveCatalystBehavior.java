package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.Position;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;

public class NoOpExplosiveCatalystBehavior extends ExplosiveCatalystBehavior {
    @Override
    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
    }

    @Override
    public boolean isNoOp() {
        return true;
    }
}
