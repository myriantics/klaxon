package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.KlaxonItems;

import java.util.function.Consumer;

public class KlaxonSmeltingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonSmeltingRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> consumer) {
        buildGearRecyclingRecipes(consumer);
        buildBlastingSmeltingRecipes(consumer);
        buildCookingRecipes(consumer);
    }

    private void buildGearRecyclingRecipes(Consumer<RecipeJsonProvider> consumer) {
        addBlastingSmeltingRecipe(consumer, Ingredient.ofItems(KlaxonItems.STEEL_HAMMER), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(consumer, Ingredient.ofItems(Items.FLINT_AND_STEEL), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(consumer, Ingredient.ofItems(KlaxonItems.STEEL_HELMET), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(consumer, Ingredient.ofItems(KlaxonItems.STEEL_CHESTPLATE), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(consumer, Ingredient.ofItems(KlaxonItems.STEEL_LEGGINGS), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(consumer, Ingredient.ofItems(KlaxonItems.STEEL_BOOTS), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
    }

    private void buildBlastingSmeltingRecipes(Consumer<RecipeJsonProvider> consumer) {
    }

    private void buildCookingRecipes(Consumer<RecipeJsonProvider> consumer) {
        addOreProcessingCookingRecipe(consumer, Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_MIXTURE), new ItemStack(KlaxonItems.CRUDE_STEEL_INGOT), 1.0f, 150, null, null);
    }
}
