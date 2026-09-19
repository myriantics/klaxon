package net.myriantics.klaxon.mechanics.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;

public interface MachineConfiguration {

    Codec<MachineConfiguration> CODEC = KlaxonBuiltInRegistries.MACHINE_CONFIGURATION_TYPE
            .byNameCodec()
            .dispatch("configuration", MachineConfiguration::getType, MachineConfigurationType::codec);

    MachineConfigurationType<? extends MachineConfiguration> getType();
}
