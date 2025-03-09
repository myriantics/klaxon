package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;

public class KlaxonRecipeOverrideProvider extends KlaxonRecipeSubProvider {
    public KlaxonRecipeOverrideProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(RecipeExporter exporter) {
        buildOverrideRecipes(exporter);
    }

    private void buildOverrideRecipes(RecipeExporter exporter) {
        addOverrideRecipe(exporter, Identifier.ofVanilla("flint_and_steel"));
    }
}
