package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;

public class BedlikeExplosiveCatalystBehavior extends DimensionTypeDependentExplosiveCatalystBehavior {
    public BedlikeExplosiveCatalystBehavior(TagKey<DimensionType> tagKey) {
        super(tagKey);
    }

    @Override
    protected boolean blocksExplosion(ExplosiveCatalystContext context) {
        return context.level().dimensionType().bedWorks() || super.blocksExplosion(context);
    }
}
