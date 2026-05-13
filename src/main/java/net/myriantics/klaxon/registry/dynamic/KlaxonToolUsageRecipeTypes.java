package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonToolUsageRecipeTypes {

    public static final ResourceKey<ToolUsageRecipeType> HAMMERING = register("hammering");
    public static final ResourceKey<ToolUsageRecipeType> WIRECUTTING = register("wirecutting");

    private static ResourceKey<ToolUsageRecipeType> register(String name) {
        return ResourceKey.create(KlaxonRegistries.TOOL_USAGE_RECIPE_TYPE, KlaxonCommon.locate(name));
    }
}
