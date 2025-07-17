package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.block.SaplingGenerator;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.DirectionalSaplingGenerator;
import net.myriantics.klaxon.datagen.KlaxonFeatureProvider;

import java.util.Optional;

public class KlaxonSaplingGenerators {
    public static final SaplingGenerator EMPTY = new SaplingGenerator(
            "empty",
            0.0f,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    public static final DirectionalSaplingGenerator HALLNOX = new DirectionalSaplingGenerator(
            "hallnox",
            Optional.of(KlaxonFeatureProvider.UPRIGHT_DRY_HALLNOX_GROWTH),
            Optional.of(KlaxonFeatureProvider.DOWNRIGHT_HALLNOX_GROWTH),
            Optional.of(KlaxonFeatureProvider.NORTH_HORIZONTAL_HALLNOX_GROWTH),
            Optional.of(KlaxonFeatureProvider.EAST_HORIZONTAL_HALLNOX_GROWTH),
            Optional.of(KlaxonFeatureProvider.SOUTH_HORIZONTAL_HALLNOX_GROWTH),
            Optional.of(KlaxonFeatureProvider.WEST_HORIZONTAL_HALLNOX_GROWTH)
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Sapling Generators!");
    }
}
