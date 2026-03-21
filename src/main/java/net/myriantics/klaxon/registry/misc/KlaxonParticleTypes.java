package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonParticleTypes {
    public static final SimpleParticleType HALLNOX_POD_DRIP = register("hallnox_drip", false);
    public static final SimpleParticleType NETHER_REACTION_EXPLOSION = register("nether_reaction_explosion", true);
    public static final SimpleParticleType NETHER_REACTION_EXPLOSION_EMITTER = register("nether_reaction_explosion_emitter", true);

    private static SimpleParticleType register(String name, boolean alwaysShow) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, KlaxonCommon.locate(name), FabricParticleTypes.simple(alwaysShow));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Particles!");
    }
}
