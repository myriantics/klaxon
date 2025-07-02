package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.block.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.Optional;

public class KlaxonSaplingGenerators {
    public static final SaplingGenerator HALLNOX = new SaplingGenerator(
            "hallnox",
            0.2f,
            Optional.empty(),
            Optional.empty(),
            Optional.of(RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, KlaxonCommon.locate("hallnox_growth"))),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Sapling Generators!");
    }
}
