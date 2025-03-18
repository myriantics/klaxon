package net.myriantics.klaxon.recipe.makeshift_crafting;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.KlaxonCodecUtils;
import org.jetbrains.annotations.Nullable;

public class MakeshiftRecipeJsonProvider implements RecipeJsonProvider {
    RecipeJsonProvider provider;
    RecipeSerializer<?> serializer;
    MakeshiftCraftingRecipe makeshiftCraftingRecipe;

    public MakeshiftRecipeJsonProvider(RecipeJsonProvider provider, RecipeSerializer<?> recipeSerializer, MakeshiftCraftingRecipe recipe) {
        this.provider = provider;
        this.serializer = recipeSerializer;
        this.makeshiftCraftingRecipe = recipe;
    }

    @Override
    public void serialize(JsonObject json) {
        provider.serialize(json);

        json.add("constant_ingredients", KlaxonCodecUtils.writeIngredientListToJson(makeshiftCraftingRecipe.getConstantIngredients()));
    }

    @Override
    public Identifier getRecipeId() {
        return provider.getRecipeId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer != null ? serializer : provider.getSerializer();
    }

    @Nullable
    @Override
    public JsonObject toAdvancementJson() {
        return provider.toAdvancementJson();
    }

    @Nullable
    @Override
    public Identifier getAdvancementId() {
        return provider.getAdvancementId();
    }
}
