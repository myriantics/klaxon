package net.myriantics.klaxon.registry;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.grapple_winch.VeinmineGroup;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;

public abstract class KlaxonDynamicRegistries {

    static {
        registerSynced(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE, ToolUsageRecipeType.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        registerSynced(KlaxonRegistryKeys.VEINMINE_GROUP, VeinmineGroup.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
    }

    private static <T> void registerSynced(RegistryKey<? extends Registry<T>> key, Codec<T> codec, DynamicRegistries.SyncOption... options) {
        DynamicRegistries.registerSynced(key, codec, options);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Dynamic Registries!");
    }
}
