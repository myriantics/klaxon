package net.myriantics.klaxon.registry.misc;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.energy.generators.turbine.TurbineGeneratorBlockEntity;
import net.myriantics.klaxon.mechanics.configuration.MachineConfiguration;
import net.myriantics.klaxon.mechanics.configuration.MachineConfigurationType;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonMachineConfigurationTypes {

    public static final Holder<MachineConfigurationType<TurbineGeneratorBlockEntity.Configuration>> TURBINE_GENERATOR = register("turbine_generator", TurbineGeneratorBlockEntity.Configuration.DIRECT_CODEC);

    @SuppressWarnings("unchecked")
    private static <T extends MachineConfiguration> Holder<MachineConfigurationType<T>> register(String name, MapCodec<T> codec) {
        return (Holder<MachineConfigurationType<T>>) (Object) Registry.registerForHolder(KlaxonBuiltInRegistries.MACHINE_CONFIGURATION_TYPE, KlaxonCommon.locate(name), new MachineConfigurationType<>(codec));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Machine Conversion Types!");
    }
}
