package net.myriantics.klaxon.recipe.hammering;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.KlaxonCodecUtils;
import org.jetbrains.annotations.Nullable;

public class HammeringRecipeJsonProvider implements RecipeJsonProvider {
    HammeringRecipe hammeringRecipe;

    public HammeringRecipeJsonProvider(HammeringRecipe hammeringRecipe) {
        this.hammeringRecipe = hammeringRecipe;
    }

    @Override
    public void serialize(JsonObject json) {
        json.add("input_ingredient", hammeringRecipe.getIngredient().toJson());
        json.add("output_stack", KlaxonCodecUtils.writeStackToJson(hammeringRecipe.getOutput(null)));
    }

    @Override
    public Identifier getRecipeId() {
        return hammeringRecipe.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.HAMMERING_RECIPE_SERIALIZER;
    }

    @Nullable
    @Override
    public JsonObject toAdvancementJson() {
        Item item = hammeringRecipe.getOutput(null).getItem();

        return Advancement.Builder.create().criterion(Registries.ITEM.getId(item).getPath(), FabricRecipeProvider.conditionsFromItem(item)).build(getAdvancementId()).createTask().toJson();
    }

    @Nullable
    @Override
    public Identifier getAdvancementId() {
        return getRecipeId().withPath("advancement/" + getRecipeId().getPath());
    }
}
