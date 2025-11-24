package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.configuration.DefaultInnateItemEnchantmentsComponent;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeLogic;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import net.myriantics.klaxon.registry.item.KlaxonDefaultItemComponentModifications;

public abstract class KlaxonEventListeners {
    public static void init() {
        DefaultItemComponentEvents.MODIFY.register(KlaxonDefaultItemComponentModifications::modify);

        LootTableEvents.MODIFY.register(KlaxonLootTableModifications::modify);

        ServerLifecycleEvents.SERVER_STARTED.register(ToolUsageRecipeLogic::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ToolUsageRecipeLogic::onDatapackReload);
        CommonLifecycleEvents.TAGS_LOADED.register(ToolUsageRecipeLogic::onTagsLoaded);

        ServerLifecycleEvents.SERVER_STARTED.register(WorldItemApplicationRecipeLogic::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(WorldItemApplicationRecipeLogic::onDatapackReload);
        CommonLifecycleEvents.TAGS_LOADED.register(WorldItemApplicationRecipeLogic::onTagsLoaded);

        ServerLifecycleEvents.SERVER_STARTED.register(DefaultInnateItemEnchantmentsComponent::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(DefaultInnateItemEnchantmentsComponent::onDataPackReload);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Event Listeners!");
    }
}
