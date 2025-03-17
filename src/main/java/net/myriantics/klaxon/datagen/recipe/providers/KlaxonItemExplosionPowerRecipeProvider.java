package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;

import java.util.function.Consumer;

public class KlaxonItemExplosionPowerRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonItemExplosionPowerRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> consumer) {
        buildItemExplosionPowerRecipes(consumer);
    }

    private void buildItemExplosionPowerRecipes(Consumer<RecipeJsonProvider> consumer) {
        addItemExplosionPowerRecipe(consumer, Ingredient.ofItems(Items.DRAGON_BREATH), 1.5, false, false);
        addItemExplosionPowerRecipe(consumer, Ingredient.ofItems(Items.END_CRYSTAL), 6.0, false, false);
        addItemExplosionPowerRecipe(consumer, Ingredient.ofItems(Items.FIRE_CHARGE), 0.5, true, false);
        addItemExplosionPowerRecipe(consumer, Ingredient.ofItems(Items.GUNPOWDER), 0.3, false, false);
        addItemExplosionPowerRecipe(consumer, Ingredient.ofItems(Items.TNT), 4.0, false, false);
        addItemExplosionPowerRecipe(consumer, Ingredient.ofItems(Items.TNT_MINECART), 5.0, false, false);

        // meme recipes
        addItemExplosionPowerRecipe(consumer, Ingredient.ofItems(Items.CREEPER_SPAWN_EGG), 3.0, false, true);
        addItemExplosionPowerRecipe(consumer, Ingredient.ofItems(Items.GHAST_SPAWN_EGG), 3.5, true, true);
    }
}
