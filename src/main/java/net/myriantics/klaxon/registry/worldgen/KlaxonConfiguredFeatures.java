package net.myriantics.klaxon.registry.worldgen;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> UPRIGHT_DRY_HALLNOX_GROWTH = of("upright_dry_hallnox_growth");

    private static RegistryKey<ConfiguredFeature<?, ?>> of(String path) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, KlaxonCommon.locate(path));
    }
}
