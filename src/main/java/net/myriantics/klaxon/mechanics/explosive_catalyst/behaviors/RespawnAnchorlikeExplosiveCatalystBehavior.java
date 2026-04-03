package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;

public class RespawnAnchorlikeExplosiveCatalystBehavior extends DimensionTypeDependentExplosiveCatalystBehavior {
    public RespawnAnchorlikeExplosiveCatalystBehavior(TagKey<DimensionType> tagKey) {
        super(tagKey);
    }

    @Override
    protected boolean fallbackCheck(ExplosiveCatalystContext context) {
        return context.level().dimensionType().respawnAnchorWorks();
    }
}
