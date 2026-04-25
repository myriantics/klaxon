package net.myriantics.klaxon.registry.misc;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeSerializer;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipe;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipeSerializer;
import net.myriantics.klaxon.recipe.makeshift_crafting.shaped.MakeshiftShapedCraftingRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shaped.MakeshiftShapedCraftingRecipeSerializer;
import net.myriantics.klaxon.recipe.makeshift_crafting.shapeless.MakeshiftShapelessCraftingRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shapeless.MakeshiftShapelessCraftingRecipeSerializer;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipe;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipeSerializer;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeSerializer;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipe;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeSerializer;

// recipe code structure yoinked from spectrums github
public abstract class KlaxonRecipeTypes {
    public static final String BLAST_PROCESSING_RECIPE_ID = "blast_processing";
    public static RecipeSerializer<BlastProcessingRecipe> BLAST_PROCESSING_RECIPE_SERIALIZER =
            registerSerializer(BLAST_PROCESSING_RECIPE_ID, new BlastProcessingRecipeSerializer());
    public static RecipeType<BlastProcessingRecipe> BLAST_PROCESSING =
            registerRecipeType(BLAST_PROCESSING_RECIPE_ID);

    public static final String TOOL_USAGE_RECIPE_ID = "tool_usage";
    public static RecipeSerializer<ToolUsageRecipe> TOOL_USAGE_RECIPE_SERIALIZER =
            registerSerializer(TOOL_USAGE_RECIPE_ID, new ToolUsageRecipeSerializer());
    public static RecipeType<ToolUsageRecipe> TOOL_USAGE = registerRecipeType(TOOL_USAGE_RECIPE_ID);

    public static final String WORLD_ITEM_APPLICATION_RECIPE_ID = "world_item_application";
    public static RecipeSerializer<WorldItemApplicationRecipe> WORLD_ITEM_APPLICATION_RECIPE_SERIALIZER =
            registerSerializer(WORLD_ITEM_APPLICATION_RECIPE_ID, new WorldItemApplicationRecipeSerializer());
    public static RecipeType<WorldItemApplicationRecipe> WORLD_ITEM_APPLICATION =
            registerRecipeType(WORLD_ITEM_APPLICATION_RECIPE_ID);

    public static final String NETHER_REACTION_RECIPE_ID = "nether_reaction";
    public static RecipeSerializer<NetherReactionRecipe> NETHER_REACTION_RECIPE_SERIALIZER =
            registerSerializer(NETHER_REACTION_RECIPE_ID, new NetherReactionRecipeSerializer());
    public static RecipeType<NetherReactionRecipe> NETHER_REACTION =
            registerRecipeType(NETHER_REACTION_RECIPE_ID);

    public static final String EXPLOSIVE_CATALYST_DEFINITION_ID = "explosive_catalyst_definition";
    public static RecipeSerializer<ExplosiveCatalystDefinitionRecipe> EXPLOSIVE_CATALYST_DEFINITION_RECIPE_SERIALIZER =
            registerSerializer(EXPLOSIVE_CATALYST_DEFINITION_ID, new ExplosiveCatalystDefinitionRecipeSerializer());
    public static RecipeType<ExplosiveCatalystDefinitionRecipe> EXPLOSIVE_CATALYST_DEFINITION =
            registerRecipeType(EXPLOSIVE_CATALYST_DEFINITION_ID);

    public static final String MAKESHIFT_SHAPED_CRAFTING_ID = "makeshift_shaped";
    public static RecipeSerializer<MakeshiftShapedCraftingRecipe> MAKESHIFT_SHAPED_CRAFTING_RECIPE_SERIALIZER =
            registerSerializer(MAKESHIFT_SHAPED_CRAFTING_ID, new MakeshiftShapedCraftingRecipeSerializer());

    public static final String MAKESHIFT_SHAPELESS_CRAFTING_ID = "makeshift_shapeless";
    public static RecipeSerializer<MakeshiftShapelessCraftingRecipe> MAKESHIFT_SHAPELESS_CRAFTING_RECIPE_SERIALIZER =
            registerSerializer(MAKESHIFT_SHAPELESS_CRAFTING_ID, new MakeshiftShapelessCraftingRecipeSerializer());

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerSerializer(String id, S serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, KlaxonCommon.locate(id), serializer);
    }

    static <T extends Recipe<?>> RecipeType<T> registerRecipeType(String id) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, KlaxonCommon.locate(id), new RecipeType<T>() {
            @Override
            public String toString() {
                return "klaxon:" + id;
            }
        });
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Recipe Types!");
    }
}
