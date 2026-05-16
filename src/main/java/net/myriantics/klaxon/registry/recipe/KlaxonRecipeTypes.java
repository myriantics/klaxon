package net.myriantics.klaxon.registry.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.StandardBlastProcessingRecipe;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipe;

public abstract class KlaxonRecipeTypes {
    public static Holder<RecipeType<BlastProcessingRecipe>> BLAST_PROCESSING = register("blast_processing");
    public static Holder<RecipeType<ToolUsageRecipe>> TOOL_USAGE = register("tool_usage");
    public static Holder<RecipeType<WorldItemApplicationRecipe>> WORLD_ITEM_APPLICATION = register("world_item_application");
    public static Holder<RecipeType<NetherReactionRecipe>> NETHER_REACTION = register("nether_reaction");

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> Holder<RecipeType<T>> register(String id) {
        return (Holder<RecipeType<T>>) (Object) Registry.registerForHolder(BuiltInRegistries.RECIPE_TYPE, KlaxonCommon.locate(id), new RecipeType<T>() {
            private final String location = KlaxonCommon.locate(id).toString();

            @Override
            public String toString() {
                return this.location;
            }
        });
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Recipe Types!");
    }
}
