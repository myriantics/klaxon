package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.minecraft.KlaxonFeatures;
import net.myriantics.klaxon.worldgen.features.hallnox.UprightDryHallnoxGrowthFeatureConfig;

import java.util.concurrent.CompletableFuture;

// https://gist.github.com/Linguardium/b81e85b3541429bbd3ca63a93b24485f used as reference
public class KlaxonFeatureProvider extends FabricDynamicRegistryProvider {
    public KlaxonFeatureProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        entries.addAll(wrapperLookup.getWrapperOrThrow(RegistryKeys.CONFIGURED_FEATURE));
        entries.addAll(wrapperLookup.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE));
    }

    @Override
    public String getName() {
        return KlaxonCommon.MOD_ID + "_feature_provider";
    }

    public static void generateConfiguredFeatures(Registerable<ConfiguredFeature<?, ?>> registerable) {
        registerable.register(
                configuredFeatureKey("hallnox_growth"),
                new ConfiguredFeature<>(KlaxonFeatures.HALLNOX_GROWTH,
                        new UprightDryHallnoxGrowthFeatureConfig(
                                BlockPredicate.alwaysTrue()
                        )
                )
        );
    }

    public static void generatePlacedFeatures(Registerable<PlacedFeature> registerable) {

    }

    private static RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey(String path) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, KlaxonCommon.locate(path));
    }

    private static RegistryKey<PlacedFeature> placedFeatureKey(String path) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, KlaxonCommon.locate(path));
    }
}
