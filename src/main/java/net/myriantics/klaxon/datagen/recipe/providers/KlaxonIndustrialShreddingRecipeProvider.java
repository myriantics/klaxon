package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.myriantics.klaxon.datagen.NamedIngredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;

public class KlaxonIndustrialShreddingRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonIndustrialShreddingRecipeProvider(KlaxonRecipeProvider provider, RecipeOutput exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
    }
}
