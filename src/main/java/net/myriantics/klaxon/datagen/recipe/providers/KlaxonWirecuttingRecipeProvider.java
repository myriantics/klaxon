package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;

public class KlaxonWirecuttingRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonWirecuttingRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildWirecuttingRecipes();
    }

    private void buildWirecuttingRecipes() {
        addWirecuttingRecipe(Ingredient.fromTag(ItemTags.WOOL), new ItemStack(Items.STRING, 2));
        addWirecuttingRecipe(Ingredient.fromTag(ItemTags.WOOL_CARPETS), new ItemStack(Items.STRING, 1));
    }
}
