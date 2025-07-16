package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.worldgen.features.hallnox.HorizontalDryHallnoxGrowthFeature;
import net.myriantics.klaxon.worldgen.features.hallnox.HorizontalDryHallnoxGrowthFeatureConfig;
import net.myriantics.klaxon.worldgen.features.hallnox.UprightDryHallnoxGrowthFeature;
import net.myriantics.klaxon.worldgen.features.hallnox.UprightDryHallnoxGrowthFeatureConfig;

public class KlaxonFeatures {
    public static final Feature<UprightDryHallnoxGrowthFeatureConfig> UPRIGHT_DRY_HALLNOX_GROWTH = register("upright_dry_hallnox_growth", new UprightDryHallnoxGrowthFeature(UprightDryHallnoxGrowthFeatureConfig.CODEC));
    public static final Feature<HorizontalDryHallnoxGrowthFeatureConfig> HORIZONTAL_DRY_HALLNOX_GROWTH = register("horizontal_dry_hallnox_growth", new HorizontalDryHallnoxGrowthFeature(HorizontalDryHallnoxGrowthFeatureConfig.CODEC));

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(Registries.FEATURE, KlaxonCommon.locate(name), feature);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Placed Features!");
    }
}
