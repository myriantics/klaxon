package net.myriantics.klaxon.mechanics.explosive_catalyst.handler;

import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystHandler;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystContextParams;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;
import org.joml.Vector3f;

public class DragonsBreathExplosiveCatalystHandler extends ExplosiveCatalystHandler {
    @Override
    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
        ServerLevel level = context.level();

        AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(level, detonationPosition.x(), detonationPosition.y() - 0.25, detonationPosition.z());

        float radius = (float) data.explosionPower() / 3;
        float finalRadius = (float) data.explosionPower() / 2;

        if (context.get(KlaxonExplosiveCatalystContextParams.SOURCE_ENTITY) instanceof LivingEntity livingEntity) {
            areaEffectCloudEntity.setOwner(livingEntity);
        }
        areaEffectCloudEntity.setParticle(ParticleTypes.DRAGON_BREATH);
        areaEffectCloudEntity.setRadius(radius);
        areaEffectCloudEntity.setDuration(80);
        areaEffectCloudEntity.setRadiusPerTick((finalRadius - radius) / areaEffectCloudEntity.getDuration());
        areaEffectCloudEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));

        KlaxonServerPlayNetworkHandler.syncWorldEvent(level, new Vector3f((float) detonationPosition.x(), (float) detonationPosition.y(), (float) detonationPosition.z()), KlaxonWorldEvents.DRAGONS_BREATH_EXPLOSIVE_CATALYST_CLOUD_SPAWNS, 1);
        level.addFreshEntity(areaEffectCloudEntity);
    }
}
