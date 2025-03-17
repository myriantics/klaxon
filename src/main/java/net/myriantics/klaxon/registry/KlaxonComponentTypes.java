package net.myriantics.klaxon.registry;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.function.UnaryOperator;

public class KlaxonComponentTypes {

    public static void registerKlaxonComponents() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Data Components!");
    }
}
