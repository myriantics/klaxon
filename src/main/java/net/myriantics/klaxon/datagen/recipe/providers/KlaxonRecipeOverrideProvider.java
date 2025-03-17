package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;

import java.util.function.Consumer;

public class KlaxonRecipeOverrideProvider extends KlaxonRecipeSubProvider {
    public KlaxonRecipeOverrideProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> consumer) {
        buildOverrideRecipes(consumer);
    }

    private void buildOverrideRecipes(Consumer<RecipeJsonProvider> consumer) {
        addOverrideRecipe(consumer, Identifier.of(Identifier.DEFAULT_NAMESPACE, "flint_and_steel"));
    }
}
