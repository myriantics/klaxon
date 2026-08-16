package net.myriantics.klaxon.registry.behavior;

import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.turbine_generator.power_source.StaticTurbineGeneratorPowerSource;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonTurbineGeneratorPowerSources {

    public static final ResourceKey<StaticTurbineGeneratorPowerSource> CAMPFIRE = create("campfire/normal");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> SIGNAL_CAMPFIRE = create("campfire/signal");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> BUBBLE_COLUMN = create("bubble_column");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> SCULK_SHRIEKER = create("sculk_shrieker");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> BREWING_STAND = create("brewing_stand");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> SMOKER = create("smoker");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> FURNACE = create("furnace");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> BLAST_FURNACE = create("blast_furnace");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> VAULT = create("vault");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> TRIAL_SPAWNER_ACTIVE = create("trial_spawner/active");
    public static final ResourceKey<StaticTurbineGeneratorPowerSource> TRIAL_SPAWNER_COOLDOWN = create("trial_spawner/cooldown");

    private static ResourceKey<StaticTurbineGeneratorPowerSource> create(String name) {
        return ResourceKey.create(KlaxonRegistries.STATIC_TURBINE_GENERATOR_POWER_SOURCE, KlaxonCommon.locate(name));
    }
}
