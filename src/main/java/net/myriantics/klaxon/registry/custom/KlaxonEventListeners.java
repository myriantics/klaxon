package net.myriantics.klaxon.registry.custom;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.configuration.DefaultInnateItemEnchantmentsComponent;
import net.myriantics.klaxon.recipe.cooling.ItemCoolingRecipeLogic;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipeLogic;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;

public class KlaxonEventListeners {
    public static void init() {
        DefaultItemComponentEvents.MODIFY.register(KlaxonDefaultItemComponentModifications::modify);

        ServerLifecycleEvents.SERVER_STARTED.register(ItemCoolingRecipeLogic::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ItemCoolingRecipeLogic::onDatapackReload);
        CommonLifecycleEvents.TAGS_LOADED.register(ItemCoolingRecipeLogic::onTagsLoaded);

        ServerLifecycleEvents.SERVER_STARTED.register(ToolUsageRecipeLogic::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ToolUsageRecipeLogic::onDatapackReload);
        CommonLifecycleEvents.TAGS_LOADED.register(ToolUsageRecipeLogic::onTagsLoaded);

        ServerLifecycleEvents.SERVER_STARTED.register(DefaultInnateItemEnchantmentsComponent::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(DefaultInnateItemEnchantmentsComponent::onDataPackReload);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Event Listeners!");
    }

    public static void initClient() {
    }
}
