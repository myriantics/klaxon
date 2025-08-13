package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.configuration.DefaultInnateItemEnchantmentsComponent;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.recipe.cooling.ItemCoolingRecipeLogic;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipeLogic;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import net.myriantics.klaxon.registry.item.KlaxonDefaultItemComponentModifications;
import net.myriantics.klaxon.util.grapple_winch.GrappleWinchUtil;

public abstract class KlaxonEventListeners {
    public static void init() {
        DefaultItemComponentEvents.MODIFY.register(KlaxonDefaultItemComponentModifications::modify);

        LootTableEvents.MODIFY.register(KlaxonLootTableModifications::modify);

        ServerLifecycleEvents.SERVER_STARTED.register(ItemCoolingRecipeLogic::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ItemCoolingRecipeLogic::onDatapackReload);
        CommonLifecycleEvents.TAGS_LOADED.register(ItemCoolingRecipeLogic::onTagsLoaded);

        ServerLifecycleEvents.SERVER_STARTED.register(ToolUsageRecipeLogic::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ToolUsageRecipeLogic::onDatapackReload);
        CommonLifecycleEvents.TAGS_LOADED.register(ToolUsageRecipeLogic::onTagsLoaded);

        ServerLifecycleEvents.SERVER_STARTED.register(ManualItemApplicationRecipeLogic::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ManualItemApplicationRecipeLogic::onDatapackReload);
        CommonLifecycleEvents.TAGS_LOADED.register(ManualItemApplicationRecipeLogic::onTagsLoaded);

        ServerLifecycleEvents.SERVER_STARTED.register(DefaultInnateItemEnchantmentsComponent::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(DefaultInnateItemEnchantmentsComponent::onDataPackReload);

        ServerWorldEvents.LOAD.register(GrappleWinchUtil::onWorldLoadedServerside);
        ServerPlayConnectionEvents.JOIN.register(GrappleWinchUtil::onPlayerJoinServer);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Event Listeners!");
    }

    public static void initClient() {
    }
}
