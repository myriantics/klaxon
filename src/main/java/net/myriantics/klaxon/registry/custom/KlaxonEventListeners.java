package net.myriantics.klaxon.registry.custom;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonEventListeners {
    public static void init() {
        DefaultItemComponentEvents.MODIFY.register(KlaxonDefaultItemComponentModifications::modify);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Event Listeners!");
    }
}
