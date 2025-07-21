package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonFuelRegistry {

    public static void init() {
        FuelRegistry registry = FuelRegistry.INSTANCE;

        registry.add(KlaxonItems.FRACTURED_COAL, 400);
        registry.add(KlaxonItems.FRACTURED_CHARCOAL, 400);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Furnace Fuels!");
    }
}
