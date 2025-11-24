package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonSmeltingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonSmeltingRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildRecyclingRecipes();
        buildOmniSmeltingRecipes();
        buildCookingRecipes();
    }

    private void buildRecyclingRecipes() {
        addBlastingSmeltingRecipe(Ingredient.fromTag(KlaxonItemTags.STEEL_EQUIPMENT), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.IRON_WIRE), new ItemStack(Items.IRON_NUGGET), 1.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.IRON_PLATE), new ItemStack(Items.IRON_NUGGET), 1.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.GOLD_WIRE), new ItemStack(Items.GOLD_NUGGET), 1.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.GOLD_PLATE), new ItemStack(Items.GOLD_NUGGET), 1.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.COPPER_WIRE), new ItemStack(KlaxonItems.COPPER_NUGGET), 1.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.COPPER_PLATE), new ItemStack(KlaxonItems.COPPER_NUGGET), 1.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.STEEL_WIRE), new ItemStack(KlaxonItems.STEEL_NUGGET), 1.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_PLATE), new ItemStack(KlaxonItems.STEEL_NUGGET), 1.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_INGOT), new ItemStack(KlaxonItems.STEEL_NUGGET), 1.0f, 150, null, null);
    }

    // WHERE'S OMNIMAN WHERE IS HE
    private void buildOmniSmeltingRecipes() {
        addOmniSmeltingRecipe(Ingredient.fromTag(KlaxonItemTags.HIGH_YIELD_RUBBER_EXTRACTABLE_LOGS), new ItemStack(KlaxonItems.RUBBER_BLOCK), 1.0f, 150, null, null);
        addOmniSmeltingRecipe(Ingredient.fromTag(KlaxonItemTags.LOW_YIELD_RUBBER_EXTRACTABLE_LOGS), new ItemStack(KlaxonItems.RUBBER_GLOB), 1.0f, 150, null, null);
    }

    private void buildCookingRecipes() {
        addOreProcessingCookingRecipe(Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_MIXTURE), new ItemStack(KlaxonItems.CRUDE_STEEL_INGOT), 1.0f, 150, null, null);
    }

    private void addRubberMeltingRecipe(Ingredient ingredient, ItemConvertible output) {
        addOreProcessingCookingRecipe(ingredient, new ItemStack(output), 1.0f, 200, null, null);
    }
}