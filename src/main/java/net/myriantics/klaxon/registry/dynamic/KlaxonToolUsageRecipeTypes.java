package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

public abstract class KlaxonToolUsageRecipeTypes {

    public static final RegistryKey<ToolUsageRecipeType> HAMMERING = register("hammering");
    public static final RegistryKey<ToolUsageRecipeType> WIRECUTTING = register("wirecutting");

    private static RegistryKey<ToolUsageRecipeType> register(String name) {
        return RegistryKey.of(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE, KlaxonCommon.locate(name));
    }
}
