package net.myriantics.klaxon.registry.worldgen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.worldgen.features.hallnox.*;

public abstract class KlaxonWorldgenFeatures {
    public static final Feature<UprightHallnoxGrowthFeatureConfig> UPRIGHT_HALLNOX_GROWTH = register("upright_hallnox_growth", new UprightHallnoxGrowthFeature(UprightHallnoxGrowthFeatureConfig.CODEC));
    public static final Feature<HorizontalHallnoxGrowthFeatureConfig> HORIZONTAL_HALLNOX_GROWTH = register("horizontal_hallnox_growth", new HorizontalHallnoxGrowthFeature(HorizontalHallnoxGrowthFeatureConfig.CODEC));
    public static final Feature<DownrightHallnoxGrowthFeatureConfig> UPSIDE_DOWN_HALLNOX_GROWTH = register("downright_hallnox_growth", new DownrightHallnoxGrowthFeature(DownrightHallnoxGrowthFeatureConfig.CODEC));

    private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(BuiltInRegistries.FEATURE, KlaxonCommon.locate(name), feature);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Placed Features!");
    }
}
