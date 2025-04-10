package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.*;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.registry.custom.KlaxonBlastProcessorBehaviors;

public class KlaxonRegistries {
    public static final Registry<BlastProcessorBehavior> BLAST_PROCESSOR_BEHAVIORS =
            FabricRegistryBuilder.createDefaulted(KlaxonRegistryKeys.BLAST_PROCESSOR_BEHAVIORS, KlaxonBlastProcessorBehaviors.DEFAULT_BEHAVIOR_ID).buildAndRegister();

    public static void registerRegistries() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Registries!");
    }
}
