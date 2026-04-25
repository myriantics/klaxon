package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;

public class TntMinecartExplosiveCatalystBehavior extends DefaultExplosiveCatalystBehavior {
    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {

        float multiplier = switch (context) {
            case ExplosiveCatalystContext.Block block -> (float) block.level().getDirectSignalTo(block.getPos()) / 15;
            case ExplosiveCatalystContext.Entity entity -> entity.getEntity() == null ? 0 : Math.min((float) Math.sqrt(entity.getEntity().getDeltaMovement().length()), 5f) / 5f;
            case ExplosiveCatalystContext.Item item -> 0.0F;
        };

        return new ExplosiveCatalystData(original.behavior(), original.explosionPower() + (original.explosionPower() * multiplier), original.producesFire());
    }
}
