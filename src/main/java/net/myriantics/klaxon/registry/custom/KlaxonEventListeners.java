package net.myriantics.klaxon.registry.custom;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.cooling.ItemCoolingHelper;

public class KlaxonEventListeners {
    public static void init() {
        DefaultItemComponentEvents.MODIFY.register(KlaxonDefaultItemComponentModifications::modify);

        ServerLifecycleEvents.SERVER_STARTED.register(ItemCoolingHelper::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ItemCoolingHelper::onDatapackReload);
        CommonLifecycleEvents.TAGS_LOADED.register(ItemCoolingHelper::onTagsLoaded);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Event Listeners!");
    }
}
