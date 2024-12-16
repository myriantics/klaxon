package net.myriantics.klaxon.api.behavior;

import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.type.FireworkExplosionComponent;

import java.util.List;

// used to differentiate between muffled blast processor firework particles and regular ones
public class MuffledBlastProcessorFireworkParticle extends FireworksSparkParticle.FireworkParticle {
    public MuffledBlastProcessorFireworkParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ParticleManager particleManager, List<FireworkExplosionComponent> fireworkExplosions) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, particleManager, fireworkExplosions);
    }
}