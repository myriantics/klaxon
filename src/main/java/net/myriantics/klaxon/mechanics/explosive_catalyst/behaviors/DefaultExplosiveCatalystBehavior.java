package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.BlastProcessorExplosionBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.AbstractExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import org.jetbrains.annotations.Nullable;


public class DefaultExplosiveCatalystBehavior extends AbstractExplosiveCatalystBehavior {

    public @Nullable DamageSource getDamageSource(ExplosiveCatalystContext context) {
        return null;
    }

    @Override
    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
        if (data.explosionPower() > 0) {
            Level level = context.level();
            if (!level.isClientSide()) {
                level.explode(
                        context.getEntity(),
                        this.getDamageSource(context),
                        new BlastProcessorExplosionBehavior(modifyWorld),
                        detonationPosition.x(),
                        detonationPosition.y(),
                        detonationPosition.z(),
                        (float) data.explosionPower(),
                        modifyWorld && data.producesFire(),
                        Level.ExplosionInteraction.BLOCK,
                        ParticleTypes.EXPLOSION,
                        ParticleTypes.EXPLOSION_EMITTER,
                        SoundEvents.GENERIC_EXPLODE
                );
            }
        }
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        return original;
    }
}