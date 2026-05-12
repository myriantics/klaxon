package net.myriantics.klaxon.registry.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipe;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipe;

public abstract class KlaxonRecipeTypes {
    public static final String BLAST_PROCESSING_RECIPE_ID = "blast_processing";
    public static RecipeType<BlastProcessingRecipe> BLAST_PROCESSING =
            registerRecipeType(BLAST_PROCESSING_RECIPE_ID);

    public static final String TOOL_USAGE_RECIPE_ID = "tool_usage";
    public static RecipeType<ToolUsageRecipe> TOOL_USAGE = registerRecipeType(TOOL_USAGE_RECIPE_ID);

    public static final String WORLD_ITEM_APPLICATION_RECIPE_ID = "world_item_application";
    public static RecipeType<WorldItemApplicationRecipe> WORLD_ITEM_APPLICATION =
            registerRecipeType(WORLD_ITEM_APPLICATION_RECIPE_ID);

    public static final String NETHER_REACTION_RECIPE_ID = "nether_reaction";
    public static RecipeType<NetherReactionRecipe> NETHER_REACTION =
            registerRecipeType(NETHER_REACTION_RECIPE_ID);

    public static final String EXPLOSIVE_CATALYST_DEFINITION_ID = "explosive_catalyst_definition";
    public static RecipeType<ExplosiveCatalystDefinitionRecipe> EXPLOSIVE_CATALYST_DEFINITION =
            registerRecipeType(EXPLOSIVE_CATALYST_DEFINITION_ID);

    public static final String MAKESHIFT_SHAPED_CRAFTING_ID = "makeshift_shaped";

    public static final String MAKESHIFT_SHAPELESS_CRAFTING_ID = "makeshift_shapeless";

    private static <T extends Recipe<?>> RecipeType<T> registerRecipeType(String id) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, KlaxonCommon.locate(id), new RecipeType<T>() {
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
