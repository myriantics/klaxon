package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeSerializer;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipe;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipeSerializer;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipe;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipeSerializer;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipeSerializer;
import net.myriantics.klaxon.recipe.makeshift_crafting.shaped.MakeshiftShapedCraftingRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shaped.MakeshiftShapedCraftingRecipeSerializer;
import net.myriantics.klaxon.recipe.makeshift_crafting.shapeless.MakeshiftShapelessCraftingRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shapeless.MakeshiftShapelessCraftingRecipeSerializer;

// recipe code structure yoinked from spectrums github
public class KlaxonRecipeTypes {
    public static final String BLAST_PROCESSING_RECIPE_ID = "blast_processing";
    public static RecipeSerializer<BlastProcessingRecipe> BLAST_PROCESSING_RECIPE_SERIALIZER =
            registerSerializer(BLAST_PROCESSING_RECIPE_ID, new BlastProcessingRecipeSerializer());
    public static RecipeType<BlastProcessingRecipe> BLAST_PROCESSING =
            registerRecipeType(BLAST_PROCESSING_RECIPE_ID);

    public static final String BLAST_PROCESSOR_BEHAVIOR_RECIPE_ID = "blast_processor_behavior";
    public static RecipeSerializer<BlastProcessorBehaviorRecipe> BLAST_PROCESSOR_BEHAVIOR_RECIPE_SERIALIZER =
            registerSerializer(BLAST_PROCESSOR_BEHAVIOR_RECIPE_ID, new BlastProcessorBehaviorRecipeSerializer());
    public static RecipeType<BlastProcessorBehaviorRecipe> BLAST_PROCESSOR_BEHAVIOR =
            registerRecipeType(BLAST_PROCESSOR_BEHAVIOR_RECIPE_ID);

    public static final String HAMMERING_RECIPE_ID = "hammering";
    public static RecipeSerializer<HammeringRecipe> HAMMERING_RECIPE_SERIALIZER =
            registerSerializer(HAMMERING_RECIPE_ID, new HammeringRecipeSerializer());
    public static RecipeType<HammeringRecipe> HAMMERING =
            registerRecipeType(HAMMERING_RECIPE_ID);

    public static final String ITEM_EXPLOSION_POWER_RECIPE_ID = "item_explosion_power";
    public static RecipeSerializer<ItemExplosionPowerRecipe> ITEM_EXPLOSION_POWER_RECIPE_SERIALIZER =
            registerSerializer(ITEM_EXPLOSION_POWER_RECIPE_ID, new ItemExplosionPowerRecipeSerializer());
    public static RecipeType<ItemExplosionPowerRecipe> ITEM_EXPLOSION_POWER =
            registerRecipeType(ITEM_EXPLOSION_POWER_RECIPE_ID);

    public static final String MAKESHIFT_SHAPED_CRAFTING_ID = "makeshift_shaped";
    public static RecipeSerializer<MakeshiftShapedCraftingRecipe> MAKESHIFT_SHAPED_CRAFTING_RECIPE_SERIALIZER =
            registerSerializer(MAKESHIFT_SHAPED_CRAFTING_ID, new MakeshiftShapedCraftingRecipeSerializer());

    public static final String MAKESHIFT_SHAPELESS_CRAFTING_ID = "makeshift_shapeless";
    public static RecipeSerializer<MakeshiftShapelessCraftingRecipe> MAKESHIFT_SHAPELESS_CRAFTING_RECIPE_SERIALIZER =
            registerSerializer(MAKESHIFT_SHAPELESS_CRAFTING_ID, new MakeshiftShapelessCraftingRecipeSerializer());

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerSerializer(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, KlaxonCommon.locate(id), serializer);
    }

    static <T extends Recipe<?>> RecipeType<T> registerRecipeType(String id) {
        return Registry.register(Registries.RECIPE_TYPE, KlaxonCommon.locate(id), new RecipeType<T>() {
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
