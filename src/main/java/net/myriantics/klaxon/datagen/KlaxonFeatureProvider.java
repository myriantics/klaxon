package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.worldgen.KlaxonWorldgenFeatures;
import net.myriantics.klaxon.worldgen.features.hallnox.HorizontalHallnoxGrowthFeatureConfig;
import net.myriantics.klaxon.worldgen.features.hallnox.UprightHallnoxGrowthFeatureConfig;
import net.myriantics.klaxon.worldgen.features.hallnox.DownrightHallnoxGrowthFeatureConfig;

import java.util.concurrent.CompletableFuture;

// https://gist.github.com/Linguardium/b81e85b3541429bbd3ca63a93b24485f used as reference
public class KlaxonFeatureProvider extends FabricDynamicRegistryProvider {
    public KlaxonFeatureProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> UPRIGHT_DRY_HALLNOX_GROWTH = configuredFeatureKey(
            "upright_dry_hallnox_growth");
    public static RegistryKey<ConfiguredFeature<?, ?>> DOWNRIGHT_HALLNOX_GROWTH = configuredFeatureKey(
            "downright_hallnox_growth");
    public static RegistryKey<ConfiguredFeature<?, ?>> NORTH_HORIZONTAL_HALLNOX_GROWTH = configuredFeatureKey(
            "north_horizontal_hallnox_growth");
    public static RegistryKey<ConfiguredFeature<?, ?>> EAST_HORIZONTAL_HALLNOX_GROWTH = configuredFeatureKey(
            "east_horizontal_hallnox_growth");
    public static RegistryKey<ConfiguredFeature<?, ?>> WEST_HORIZONTAL_HALLNOX_GROWTH = configuredFeatureKey(
            "west_horizontal_hallnox_growth");
    public static RegistryKey<ConfiguredFeature<?, ?>> SOUTH_HORIZONTAL_HALLNOX_GROWTH = configuredFeatureKey(
            "south_horizontal_hallnox_growth");

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
        BlockPredicate replaceableBlocks = BlockPredicate.matchingBlocks(
                Blocks.OAK_SAPLING,
                Blocks.SPRUCE_SAPLING,
                Blocks.BIRCH_SAPLING,
                Blocks.JUNGLE_SAPLING,
                Blocks.ACACIA_SAPLING,
                Blocks.CHERRY_SAPLING,
                Blocks.DARK_OAK_SAPLING,
                Blocks.MANGROVE_PROPAGULE,
                Blocks.DANDELION,
                Blocks.TORCHFLOWER,
                Blocks.POPPY,
                Blocks.BLUE_ORCHID,
                Blocks.ALLIUM,
                Blocks.AZURE_BLUET,
                Blocks.RED_TULIP,
                Blocks.ORANGE_TULIP,
                Blocks.WHITE_TULIP,
                Blocks.PINK_TULIP,
                Blocks.OXEYE_DAISY,
                Blocks.CORNFLOWER,
                Blocks.WITHER_ROSE,
                Blocks.LILY_OF_THE_VALLEY,
                Blocks.BROWN_MUSHROOM,
                Blocks.RED_MUSHROOM,
                Blocks.WHEAT,
                Blocks.SUGAR_CANE,
                Blocks.ATTACHED_PUMPKIN_STEM,
                Blocks.ATTACHED_MELON_STEM,
                Blocks.PUMPKIN_STEM,
                Blocks.MELON_STEM,
                Blocks.LILY_PAD,
                Blocks.NETHER_WART,
                Blocks.COCOA,
                Blocks.CARROTS,
                Blocks.POTATOES,
                Blocks.CHORUS_PLANT,
                Blocks.CHORUS_FLOWER,
                Blocks.TORCHFLOWER_CROP,
                Blocks.PITCHER_CROP,
                Blocks.BEETROOTS,
                Blocks.SWEET_BERRY_BUSH,
                Blocks.WARPED_FUNGUS,
                Blocks.CRIMSON_FUNGUS,
                Blocks.WEEPING_VINES,
                Blocks.WEEPING_VINES_PLANT,
                Blocks.TWISTING_VINES,
                Blocks.TWISTING_VINES_PLANT,
                Blocks.CAVE_VINES,
                Blocks.CAVE_VINES_PLANT,
                Blocks.SPORE_BLOSSOM,
                Blocks.AZALEA,
                Blocks.FLOWERING_AZALEA,
                Blocks.MOSS_CARPET,
                Blocks.PINK_PETALS,
                Blocks.BIG_DRIPLEAF,
                Blocks.BIG_DRIPLEAF_STEM,
                Blocks.SMALL_DRIPLEAF
        );

        registerable.register(
                UPRIGHT_DRY_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonWorldgenFeatures.UPRIGHT_HALLNOX_GROWTH,
                        new UprightHallnoxGrowthFeatureConfig(
                                replaceableBlocks,
                                6,
                                KlaxonBlocks.HALLNOX_STEM.getDefaultState(),
                                KlaxonBlocks.HALLNOX_HYPHAE.getDefaultState(),
                                KlaxonBlocks.HALLNOX_WART_BLOCK.getDefaultState(),
                                KlaxonBlocks.HALLNOX_POD.getDefaultState()
                        )
                )
        );
        registerable.register(
                DOWNRIGHT_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonWorldgenFeatures.UPSIDE_DOWN_HALLNOX_GROWTH,
                        new DownrightHallnoxGrowthFeatureConfig(
                                replaceableBlocks,
                                8,
                                KlaxonBlocks.HALLNOX_HYPHAE.getDefaultState(),
                                KlaxonBlocks.HALLNOX_STEM.getDefaultState(),
                                KlaxonBlocks.HALLNOX_WART_BLOCK.getDefaultState(),
                                KlaxonBlocks.HALLNOX_POD.getDefaultState()
                        )
                )
        );
        registerable.register(
                NORTH_HORIZONTAL_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonWorldgenFeatures.HORIZONTAL_HALLNOX_GROWTH,
                        new HorizontalHallnoxGrowthFeatureConfig(
                                replaceableBlocks,
                                Direction.NORTH,
                                KlaxonBlocks.HALLNOX_STEM.getDefaultState(),
                                KlaxonBlocks.HALLNOX_WART_BLOCK.getDefaultState(),
                                KlaxonBlocks.HALLNOX_POD.getDefaultState()
                        )
                )
        );
        registerable.register(
                EAST_HORIZONTAL_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonWorldgenFeatures.HORIZONTAL_HALLNOX_GROWTH,
                        new HorizontalHallnoxGrowthFeatureConfig(
                                replaceableBlocks,
                                Direction.EAST,
                                KlaxonBlocks.HALLNOX_STEM.getDefaultState(),
                                KlaxonBlocks.HALLNOX_WART_BLOCK.getDefaultState(),
                                KlaxonBlocks.HALLNOX_POD.getDefaultState()
                        )
                )
        );
        registerable.register(
                WEST_HORIZONTAL_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonWorldgenFeatures.HORIZONTAL_HALLNOX_GROWTH,
                        new HorizontalHallnoxGrowthFeatureConfig(
                                replaceableBlocks,
                                Direction.WEST,
                                KlaxonBlocks.HALLNOX_STEM.getDefaultState(),
                                KlaxonBlocks.HALLNOX_WART_BLOCK.getDefaultState(),
                                KlaxonBlocks.HALLNOX_POD.getDefaultState()
                        )
                )
        );
        registerable.register(
                SOUTH_HORIZONTAL_HALLNOX_GROWTH,
                new ConfiguredFeature<>(KlaxonWorldgenFeatures.HORIZONTAL_HALLNOX_GROWTH,
                        new HorizontalHallnoxGrowthFeatureConfig(
                                replaceableBlocks,
                                Direction.SOUTH,
                                KlaxonBlocks.HALLNOX_STEM.getDefaultState(),
                                KlaxonBlocks.HALLNOX_WART_BLOCK.getDefaultState(),
                                KlaxonBlocks.HALLNOX_POD.getDefaultState()
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
