package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.mixin.minecraft.blast_processor_behaviors.WindChargeInvoker;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class WindChargeExplosiveCatalystBehavior extends DefaultExplosiveCatalystBehavior {

    @Override
    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
        Level level = context.level();
        WindCharge windCharge = new WindCharge(level, detonationPosition.x(), detonationPosition.y(), detonationPosition.z(), Vec3.ZERO);
        WindChargeInvoker windChargeInvoker = ((WindChargeInvoker) windCharge);

        level.addFreshEntity(windCharge);

        // explode
        if (modifyWorld) {
            windChargeInvoker.invokeExplode(new Vec3(detonationPosition.x(), detonationPosition.y(), detonationPosition.z()));
        } else {
            level.explode(
                    windCharge,
                    null,
                    AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR,
                    detonationPosition.x(),
                    detonationPosition.y(),
                    detonationPosition.z(),
                    1.2F,
                    false,
                    // replace ExplosionSourceType.TRIGGER to prevent world griefing
                    Level.ExplosionInteraction.NONE,
                    ParticleTypes.GUST_EMITTER_SMALL,
                    ParticleTypes.GUST_EMITTER_LARGE,
                    SoundEvents.WIND_CHARGE_BURST
            );
        }

        // discard
        windCharge.discard();
    }
}
