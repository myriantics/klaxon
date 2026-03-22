package net.myriantics.klaxon.registry.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.worldgen.features.hallnox.*;

public abstract class KlaxonWorldgenFeatures {
    public static final Holder<Feature<UprightHallnoxGrowthFeatureConfig>> UPRIGHT_HALLNOX_GROWTH = register("upright_hallnox_growth", new UprightHallnoxGrowthFeature(UprightHallnoxGrowthFeatureConfig.CODEC));
    public static final Holder<Feature<HorizontalHallnoxGrowthFeatureConfig>> HORIZONTAL_HALLNOX_GROWTH = register("horizontal_hallnox_growth", new HorizontalHallnoxGrowthFeature(HorizontalHallnoxGrowthFeatureConfig.CODEC));
    public static final Holder<Feature<DownrightHallnoxGrowthFeatureConfig>> UPSIDE_DOWN_HALLNOX_GROWTH = register("downright_hallnox_growth", new DownrightHallnoxGrowthFeature(DownrightHallnoxGrowthFeatureConfig.CODEC));

    @SuppressWarnings("unchecked")
    private static <C extends FeatureConfiguration, F extends Feature<C>> Holder<F> register(String name, F feature) {
        return (Holder<F>) (Object) Registry.registerForHolder(BuiltInRegistries.FEATURE, KlaxonCommon.locate(name), feature);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Placed Features!");
    }
}
