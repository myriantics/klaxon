package net.myriantics.klaxon.recipes;

import io.netty.handler.codec.DecoderException;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorRecipe;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorRecipeSerializer;
import net.myriantics.klaxon.recipes.hammer.HammerRecipe;
import net.myriantics.klaxon.recipes.hammer.HammerRecipeSerializer;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipeSerializer;

// recipe code structure yoinked from spectrums github
public class KlaxonRecipeTypes {
    public static final String BLAST_PROCESSING_RECIPE_ID = "blast_processing";
    public static RecipeSerializer<BlastProcessorRecipe> BLAST_PROCESSING_RECIPE_SERIALIZER;
    public static RecipeType<BlastProcessorRecipe> BLAST_PROCESSING;

    public static final String HAMMERING_RECIPE_ID = "hammering";
    public static RecipeSerializer<HammerRecipe> HAMMERING_RECIPE_SERIALIZER;
    public static RecipeType<HammerRecipe> HAMMERING;

    public static final String ITEM_EXPLOSION_POWER_RECIPE_ID = "item_explosion_power";
    public static RecipeSerializer<ItemExplosionPowerRecipe> ITEM_EXPLOSION_POWER_RECIPE_SERIALIZER;
    public static RecipeType<ItemExplosionPowerRecipe> ITEM_EXPLOSION_POWER;

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

    public static void registerSerializer() {
        BLAST_PROCESSING_RECIPE_SERIALIZER = registerSerializer(BLAST_PROCESSING_RECIPE_ID, new BlastProcessorRecipeSerializer());
        BLAST_PROCESSING = registerRecipeType(BLAST_PROCESSING_RECIPE_ID);

        HAMMERING_RECIPE_SERIALIZER = registerSerializer(HAMMERING_RECIPE_ID, new HammerRecipeSerializer());
        HAMMERING = registerRecipeType(HAMMERING_RECIPE_ID);

        ITEM_EXPLOSION_POWER_RECIPE_SERIALIZER = registerSerializer(ITEM_EXPLOSION_POWER_RECIPE_ID, new ItemExplosionPowerRecipeSerializer());
        ITEM_EXPLOSION_POWER = registerRecipeType(ITEM_EXPLOSION_POWER_RECIPE_ID);
    }
}
