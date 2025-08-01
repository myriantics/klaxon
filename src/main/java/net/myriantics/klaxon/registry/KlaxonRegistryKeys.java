package net.myriantics.klaxon.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;

public abstract class KlaxonRegistryKeys {
    
    public static final RegistryKey<Registry<BlastProcessorCatalystBehavior>> BLAST_PROCESSOR_BEHAVIORS = of("blast_processor_behaviors");

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(KlaxonCommon.locate(id));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Registry Keys!");
    }
}
