package net.myriantics.klaxon.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;

public class KlaxonRegistryKeys {
    
    public static final RegistryKey<Registry<BlastProcessorBehavior>> BLAST_PROCESSOR_BEHAVIORS = of("blast_processor_behaviors");

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(KlaxonCommon.locate(id));
    }

    public static void registerRegistryKeys() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Registry Keys!");
    }
}
