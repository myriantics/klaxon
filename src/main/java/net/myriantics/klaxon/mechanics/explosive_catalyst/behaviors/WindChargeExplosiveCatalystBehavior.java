package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.mixin.minecraft.blast_processor_behaviors.WindChargeInvoker;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class WindChargeExplosiveCatalystBehavior extends DefaultExplosiveCatalystBehavior {

    @Override
    protected ExplosionDamageCalculator explosionDamageCalculator(ExplosiveCatalystContext context, ExplosiveCatalystData data, boolean modifyWorld) {
        return AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR;
    }

    @Override
    protected Level.ExplosionInteraction explosionInteraction(ExplosiveCatalystContext context, ExplosiveCatalystData data, boolean modifyWorld) {
        return modifyWorld ? Level.ExplosionInteraction.TRIGGER : Level.ExplosionInteraction.NONE;
    }

    @Override
    protected ParticleOptions smallExplosionParticles(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        return ParticleTypes.GUST_EMITTER_SMALL;
    }

    @Override
    protected ParticleOptions largeExplosionParticles(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        return ParticleTypes.GUST_EMITTER_LARGE;
    }

    @Override
    protected Holder<SoundEvent> explosionSound(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        return SoundEvents.WIND_CHARGE_BURST;
    }
}
