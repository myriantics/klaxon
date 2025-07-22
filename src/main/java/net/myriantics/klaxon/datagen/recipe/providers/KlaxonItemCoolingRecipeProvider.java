package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;

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
        addItemCoolingRecipe(Ingredient.ofItems(KlaxonItems.MOLTEN_RUBBER_BLOCK), KlaxonItems.RUBBER_BLOCK);
    }
}
