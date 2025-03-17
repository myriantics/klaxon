package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.KlaxonItems;

import java.util.List;
import java.util.function.Consumer;

public class KlaxonHammeringRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonHammeringRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> consumer) {
        buildHammeringRecipes(consumer);
    }

    private void buildHammeringRecipes(Consumer<RecipeJsonProvider> consumer) {
        addHammeringRecipe(consumer, Ingredient.ofItems(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 4));
        addHammeringRecipe(consumer, Ingredient.ofItems(Items.SNOWBALL), new ItemStack(Items.SNOW));

        addHammeringRecipe(consumer, Ingredient.ofItems(KlaxonItems.STEEL_INGOT), new ItemStack(KlaxonItems.STEEL_PLATE));
        addHammeringRecipe(consumer, Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_INGOT), new ItemStack(KlaxonItems.CRUDE_STEEL_PLATE));
        addHammeringRecipe(consumer, Ingredient.ofItems(Items.IRON_INGOT), new ItemStack(KlaxonItems.IRON_PLATE));
        addHammeringRecipe(consumer, Ingredient.ofItems(Items.GOLD_INGOT), new ItemStack(KlaxonItems.GOLD_PLATE));
        addHammeringRecipe(consumer, Ingredient.ofItems(Items.COPPER_INGOT), new ItemStack(KlaxonItems.COPPER_PLATE));

        // create compat recipes - done manually for now because of issues with itemstacks returning air as an id - should resolve in future
        addHammeringRecipe(consumer, Ingredient.ofItems(KlaxonDatagenPhantomItems.CREATE_BRASS_INGOT), new ItemStack(KlaxonDatagenPhantomItems.CREATE_BRASS_SHEET),
                DefaultResourceConditions.allModsLoaded(KlaxonDatagenPhantomItems.CREATE_MOD_ID));
    }
}
