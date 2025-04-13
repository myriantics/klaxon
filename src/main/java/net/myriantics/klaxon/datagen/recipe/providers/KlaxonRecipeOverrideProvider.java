package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;

public class KlaxonRecipeOverrideProvider extends KlaxonRecipeSubProvider {
    public KlaxonRecipeOverrideProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildOverrideRecipes();
    }

    private void buildOverrideRecipes() {
        addOverrideRecipe(Identifier.ofVanilla("flint_and_steel"));
        addOverrideRecipe(Identifier.ofVanilla("blast_furnace"));
    }
}
