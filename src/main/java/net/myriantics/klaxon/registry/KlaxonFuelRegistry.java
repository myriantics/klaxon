package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonFuelRegistry {

    public static void registerFuelItems() {
        FuelRegistry registry = FuelRegistry.INSTANCE;

        registry.add(KlaxonItems.FRACTURED_COAL_CHUNKS, 400);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Furnace Fuels!");
    }
}
