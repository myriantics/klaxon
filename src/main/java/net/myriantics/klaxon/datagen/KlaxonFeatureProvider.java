package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.minecraft.KlaxonFeatures;
import net.myriantics.klaxon.worldgen.features.hallnox.HorizontalDryHallnoxGrowthFeatureConfig;
import net.myriantics.klaxon.worldgen.features.hallnox.UprightDryHallnoxGrowthFeature;
import net.myriantics.klaxon.worldgen.features.hallnox.UprightDryHallnoxGrowthFeatureConfig;

import java.util.concurrent.CompletableFuture;

// https://gist.github.com/Linguardium/b81e85b3541429bbd3ca63a93b24485f used as reference
public class KlaxonFeatureProvider extends FabricDynamicRegistryProvider {
    public KlaxonFeatureProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> UPRIGHT_DRY_HALLNOX_GROWTH = configuredFeatureKey(
            "upright_dry_hallnox_growth");
    public static RegistryKey<ConfiguredFeature<?, ?>> NORTH_HORIZONTAL_DRY_HALLNOX_GROWTH = configuredFeatureKey(
            "north_horizontal_dry_hallnox_growth");
    public static RegistryKey<ConfiguredFeature<?, ?>> EAST_HORIZONTAL_DRY_HALLNOX_GROWTH = configuredFeatureKey(
            "east_horizontal_dry_hallnox_growth");
    public static RegistryKey<ConfiguredFeature<?, ?>> WEST_HORIZONTAL_DRY_HALLNOX_GROWTH = configuredFeatureKey(
            "west_horizontal_dry_hallnox_growth");
    public static RegistryKey<ConfiguredFeature<?, ?>> SOUTH_HORIZONTAL_DRY_HALLNOX_GROWTH = configuredFeatureKey(
            "south_horizontal_dry_hallnox_growth");

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
                UPRIGHT_DRY_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonFeatures.UPRIGHT_DRY_HALLNOX_GROWTH,
                        new UprightDryHallnoxGrowthFeatureConfig(
                                BlockPredicate.alwaysTrue(),
                                6
                        )
                )
        );
        registerable.register(
                NORTH_HORIZONTAL_DRY_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonFeatures.HORIZONTAL_DRY_HALLNOX_GROWTH,
                        new HorizontalDryHallnoxGrowthFeatureConfig(
                                BlockPredicate.alwaysTrue(),
                                Direction.NORTH
                        )
                )
        );
        registerable.register(
                EAST_HORIZONTAL_DRY_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonFeatures.HORIZONTAL_DRY_HALLNOX_GROWTH,
                        new HorizontalDryHallnoxGrowthFeatureConfig(
                                BlockPredicate.alwaysTrue(),
                                Direction.EAST
                        )
                )
        );
        registerable.register(
                WEST_HORIZONTAL_DRY_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonFeatures.HORIZONTAL_DRY_HALLNOX_GROWTH,
                        new HorizontalDryHallnoxGrowthFeatureConfig(
                                BlockPredicate.alwaysTrue(),
                                Direction.WEST
                        )
                )
        );
        registerable.register(
                SOUTH_HORIZONTAL_DRY_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonFeatures.HORIZONTAL_DRY_HALLNOX_GROWTH,
                        new HorizontalDryHallnoxGrowthFeatureConfig(
                                BlockPredicate.alwaysTrue(),
                                Direction.SOUTH
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
