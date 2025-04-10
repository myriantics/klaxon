package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;

public class KlaxonItemExplosionPowerRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonItemExplosionPowerRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(RecipeExporter exporter) {
        buildItemExplosionPowerRecipes(exporter);
    }

    private void buildItemExplosionPowerRecipes(RecipeExporter exporter) {
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.END_CRYSTAL), 6.0, false, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.FIRE_CHARGE), 0.5, true, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.GUNPOWDER), 0.3, false, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.TNT), 4.0, false, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.TNT_MINECART), 5.0, false, false);

        // meme recipes
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.CREEPER_SPAWN_EGG), 3.0, false, true);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.GHAST_SPAWN_EGG), 3.5, true, true);
    }
}
