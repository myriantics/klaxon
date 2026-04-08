package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.Position;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import org.jetbrains.annotations.Nullable;

public class BedlikeExplosiveCatalystBehavior extends DimensionTypeDependentExplosiveCatalystBehavior {
    public BedlikeExplosiveCatalystBehavior(TagKey<DimensionType> tagKey) {
        super(tagKey);
    }

    @Override
    protected boolean fallbackCheck(ExplosiveCatalystContext context) {
        return context.level().dimensionType().bedWorks();
    }

    @Override
    protected @Nullable DamageSource getDamageSource(ExplosiveCatalystContext context, Position position) {
        return context.level().damageSources().badRespawnPointExplosion(position instanceof Vec3 vec3 ? vec3 : new Vec3(position.x(), position.y(), position.z()));
    }
}
