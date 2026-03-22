package net.myriantics.klaxon.registry.render;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.particle.HallnoxDripParticle;
import net.myriantics.klaxon.particle.NetherReactionExplosionEmitterParticle;
import net.myriantics.klaxon.particle.NetherReactionExplosionLargeParticle;
import net.myriantics.klaxon.registry.misc.KlaxonParticleTypes;

public abstract class KlaxonParticleFactories {

    static {
        register(KlaxonParticleTypes.HALLNOX_POD_DRIP.value(), HallnoxDripParticle.HallnoxDripParticleFactory::new);
        register(KlaxonParticleTypes.NETHER_REACTION_EXPLOSION.value(), NetherReactionExplosionLargeParticle.Factory::new);
        register(KlaxonParticleTypes.NETHER_REACTION_EXPLOSION_EMITTER.value(), new NetherReactionExplosionEmitterParticle.Factory());
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Particle Factories!");
    }

    private static <T extends ParticleOptions> void register(Holder<ParticleType<T>> typeHolder, ParticleFactoryRegistry.PendingParticleFactory<T> factory) {
        register(typeHolder.value(), factory);
    }

    private static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleFactoryRegistry.PendingParticleFactory<T> factory) {
        ParticleFactoryRegistry.getInstance().register(type, factory);
    }

    private static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
        ParticleFactoryRegistry.getInstance().register(type, provider);
    }
}
