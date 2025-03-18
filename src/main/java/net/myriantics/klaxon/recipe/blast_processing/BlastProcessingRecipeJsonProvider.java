package net.myriantics.klaxon.recipe.blast_processing;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.KlaxonCodecUtils;
import org.jetbrains.annotations.Nullable;

public class BlastProcessingRecipeJsonProvider implements RecipeJsonProvider {
    private final BlastProcessingRecipe blastProcessingRecipe;

    public BlastProcessingRecipeJsonProvider(BlastProcessingRecipe recipe) {
        this.blastProcessingRecipe = recipe;
    }

    @Override
    public void serialize(JsonObject json) {
        // add ingredients and outputs
        json.add("input_ingredient", blastProcessingRecipe.getIngredient().toJson());
        json.add("output_stack", KlaxonCodecUtils.writeStackToJson(blastProcessingRecipe.getOutput(null)));

        // add explosion power data
        json.addProperty("explosion_power_min", blastProcessingRecipe.getExplosionPowerMin());
        json.addProperty("explosion_power_max", blastProcessingRecipe.getExplosionPowerMax());
    }

    @Override
    public Identifier getRecipeId() {
        return blastProcessingRecipe.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_SERIALIZER;
    }

    @Nullable
    @Override
    public JsonObject toAdvancementJson() {
        // not doin it
        return Advancement.Builder.create().toJson();
    }

    @Nullable
    @Override
    public Identifier getAdvancementId() {
        return getRecipeId().withPath("advancement/" + getRecipeId().getPath());
    }
}
