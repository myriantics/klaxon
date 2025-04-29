package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.*;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;

public class KlaxonRegistries {
    public static final Registry<BlastProcessorCatalystBehavior> BLAST_PROCESSOR_BEHAVIORS =
            FabricRegistryBuilder.createSimple(KlaxonRegistryKeys.BLAST_PROCESSOR_BEHAVIORS).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Registries!");
    }
}
