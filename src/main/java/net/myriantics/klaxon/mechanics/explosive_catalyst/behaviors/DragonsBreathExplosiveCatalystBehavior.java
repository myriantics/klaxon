package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.myriantics.klaxon.mechanics.explosive_catalyst.AbstractExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;

public class DragonsBreathExplosiveCatalystBehavior extends AbstractExplosiveCatalystBehavior {

    @Override
    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
        if (context.level() instanceof ServerLevel serverWorld) {

            AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(serverWorld, detonationPosition.x(), detonationPosition.y() - 0.25, detonationPosition.z());

            float radius = (float) data.explosionPower() / 3;
            float finalRadius = (float) data.explosionPower() / 2;

            areaEffectCloudEntity.setParticle(ParticleTypes.DRAGON_BREATH);
            areaEffectCloudEntity.setRadius(radius);
            areaEffectCloudEntity.setDuration(80);
            areaEffectCloudEntity.setRadiusPerTick((finalRadius - radius) / areaEffectCloudEntity.getDuration());
            areaEffectCloudEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));

            KlaxonServerPlayNetworkHandler.syncWorldEvent(serverWorld, BlockPos.containing(detonationPosition.x(), detonationPosition.y(), detonationPosition.z()), KlaxonWorldEvents.DRAGONS_BREATH_EXPLOSIVE_CATALYST_CLOUD_SPAWNS, 1);
            context.level().addFreshEntity(areaEffectCloudEntity);
        }
    }
}
