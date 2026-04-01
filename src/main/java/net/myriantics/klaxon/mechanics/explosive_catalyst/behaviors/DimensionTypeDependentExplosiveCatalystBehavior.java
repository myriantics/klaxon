package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

import java.util.Optional;

public class DimensionTypeDependentExplosiveCatalystBehavior extends DefaultExplosiveCatalystBehavior {
    private final TagKey<DimensionType> tagKey;

    public DimensionTypeDependentExplosiveCatalystBehavior(TagKey<DimensionType> tagKey) {
        this.tagKey = tagKey;
    }

    protected boolean blocksExplosion(ExplosiveCatalystContext context) {
        Optional<Registry<DimensionType>> reg = context.level().registryAccess().registry(Registries.DIMENSION_TYPE);
        return reg.isPresent() && reg.get().wrapAsHolder(context.level().dimensionType()).is(this.tagKey);
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
