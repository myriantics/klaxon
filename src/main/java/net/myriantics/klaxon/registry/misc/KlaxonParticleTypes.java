package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonParticleTypes {
    public static final SimpleParticleType HALLNOX_POD_DRIP = register("hallnox_drip", false);

    private static SimpleParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, KlaxonCommon.locate(name), FabricParticleTypes.simple(alwaysShow));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Particles!");
    }
}
