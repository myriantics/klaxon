package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.BlastProcessorExplosionBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import org.jetbrains.annotations.Nullable;


public class DefaultExplosiveCatalystBehavior extends ExplosiveCatalystBehavior {

    @Override
    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
        if (data.explosionPower() > 0) {
            Level level = context.level();
            if (!level.isClientSide()) {
                level.explode(
                        context.getEntity(),
                        this.getDamageSource(context, detonationPosition),
                        this.explosionDamageCalculator(context, data, modifyWorld),
                        detonationPosition.x(),
                        detonationPosition.y(),
                        detonationPosition.z(),
                        (float) data.explosionPower(),
                        modifyWorld && data.producesFire(),
                        this.explosionInteraction(context, data, modifyWorld),
                        this.smallExplosionParticles(context, data),
                        this.largeExplosionParticles(context, data),
                        this.explosionSound(context, data)
                );
            }
        }
    }

    @Nullable
    protected DamageSource getDamageSource(ExplosiveCatalystContext context, Position position) {
        return null;
    }

    protected ExplosionDamageCalculator explosionDamageCalculator(ExplosiveCatalystContext context, ExplosiveCatalystData data, boolean modifyWorld) {
        return new BlastProcessorExplosionBehavior(modifyWorld);
    }

    protected Level.ExplosionInteraction explosionInteraction(ExplosiveCatalystContext context, ExplosiveCatalystData data, boolean modifyWorld) {
        return Level.ExplosionInteraction.BLOCK;
    }

    protected ParticleOptions smallExplosionParticles(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        return ParticleTypes.EXPLOSION;
    }

    protected ParticleOptions largeExplosionParticles(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        return ParticleTypes.EXPLOSION_EMITTER;
    }

    protected Holder<SoundEvent> explosionSound(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        return SoundEvents.GENERIC_EXPLODE;
    }
}