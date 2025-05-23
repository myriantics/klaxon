package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;

public class KlaxonItemCoolingRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonItemCoolingRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildItemCoolingRecipes();
    }

    private void buildItemCoolingRecipes() {
        addItemCoolingRecipe(Ingredient.ofItems(KlaxonItems.MOLTEN_RUBBER_GLOB), KlaxonItems.RUBBER_GLOB);
        addItemCoolingRecipe(Ingredient.ofItems(KlaxonItems.MOLTEN_RUBBER_SHEET), KlaxonItems.RUBBER_SHEET);
        addItemCoolingRecipe(Ingredient.ofItems(KlaxonBlocks.MOLTEN_RUBBER_BLOCK), KlaxonBlocks.RUBBER_BLOCK);
    }
}
