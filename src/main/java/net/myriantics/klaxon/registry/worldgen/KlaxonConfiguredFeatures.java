package net.myriantics.klaxon.registry.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> UPRIGHT_DRY_HALLNOX_GROWTH = of("upright_dry_hallnox_growth");

    private static ResourceKey<ConfiguredFeature<?, ?>> of(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, KlaxonCommon.locate(path));
    }
}
