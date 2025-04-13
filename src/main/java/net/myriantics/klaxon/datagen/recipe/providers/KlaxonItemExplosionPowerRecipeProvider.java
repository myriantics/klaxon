package net.myriantics.klaxon.datagen.recipe.providers;

import com.mojang.datafixers.types.templates.Named;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.api.NamedIngredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;

public class KlaxonItemExplosionPowerRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonItemExplosionPowerRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildItemExplosionPowerRecipes();
    }

    private void buildItemExplosionPowerRecipes() {
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.END_CRYSTAL), 6.0, false, false);
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.FIRE_CHARGE), 0.5, true, false);
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.GUNPOWDER), 0.3, false, false);
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.TNT), 4.0, false, false);
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.TNT_MINECART), 5.0, false, false);

        // meme recipes
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.CREEPER_SPAWN_EGG), 3.0, false, true);
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.GHAST_SPAWN_EGG), 3.5, true, true);
    }
}
