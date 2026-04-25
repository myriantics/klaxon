package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;

import java.util.Optional;

public class DimensionTypeDependentExplosiveCatalystBehavior extends DefaultExplosiveCatalystBehavior {
    private final TagKey<DimensionType> tagKey;

    public DimensionTypeDependentExplosiveCatalystBehavior(TagKey<DimensionType> tagKey) {
        this.tagKey = tagKey;
    }

    private boolean blocksExplosion(ExplosiveCatalystContext context) {
        Optional<Registry<DimensionType>> reg = context.level().registryAccess().registry(Registries.DIMENSION_TYPE);
        if (reg.isPresent()) {
            Optional<HolderSet.Named<DimensionType>> tag = reg.get().getTag(this.tagKey);
            if (tag.isPresent() && tag.get().size() > 0) {
                return reg.get().wrapAsHolder(context.level().dimensionType()).is(this.tagKey);
            }
        }
        // if registry is missing or tag is empty, fallback to original behavior
        return this.fallbackCheck(context);
    }

    protected boolean fallbackCheck(ExplosiveCatalystContext context) {
        return false;
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        if (this.blocksExplosion(context)) {
            return ExplosiveCatalystData.ZERO;
        } else {
            return original;
        }
    }
}
