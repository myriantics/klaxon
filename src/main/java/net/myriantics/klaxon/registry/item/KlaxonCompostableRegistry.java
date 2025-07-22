package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonCompostableRegistry {
    static {
        CompostingChanceRegistry.INSTANCE.add(KlaxonItems.HALLNOX_POD, 1f);
        CompostingChanceRegistry.INSTANCE.add(KlaxonItems.HALLNOX_WART_BLOCK, 0.95f);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Compostable Items!");
    }
}
