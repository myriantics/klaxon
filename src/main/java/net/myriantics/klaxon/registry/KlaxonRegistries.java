package net.myriantics.klaxon.registry;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.Bootstrap;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.registry.custom.KlaxonBlastProcessorBehaviors;

import java.util.function.Supplier;

public class KlaxonRegistries {
    public static final Registry<BlastProcessorBehavior> BLAST_PROCESSOR_BEHAVIORS =
            FabricRegistryBuilder.createDefaulted(KlaxonRegistryKeys.BLAST_PROCESSOR_BEHAVIORS, KlaxonBlastProcessorBehaviors.DEFAULT_BEHAVIOR).buildAndRegister();
}
