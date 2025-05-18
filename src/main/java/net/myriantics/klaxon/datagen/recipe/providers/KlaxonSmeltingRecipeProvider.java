package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;

public class KlaxonSmeltingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonSmeltingRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildGearRecyclingRecipes();
        buildBlastingSmeltingRecipes();
        buildCookingRecipes();
    }

    private void buildGearRecyclingRecipes() {
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.STEEL_HAMMER), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(Items.FLINT_AND_STEEL), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.STEEL_HELMET), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.STEEL_CHESTPLATE), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.STEEL_LEGGINGS), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
        addBlastingSmeltingRecipe(Ingredient.ofItems(KlaxonItems.STEEL_BOOTS), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, 150, null, null);
    }

    private void buildBlastingSmeltingRecipes() {
        addRubberMeltingRecipe(Ingredient.ofItems(KlaxonItems.RUBBER_GLOB), KlaxonItems.MOLTEN_RUBBER_GLOB);
        addRubberMeltingRecipe(Ingredient.ofItems(KlaxonItems.RUBBER_SHEET), KlaxonItems.MOLTEN_RUBBER_SHEET);
        addRubberMeltingRecipe(Ingredient.ofItems(KlaxonBlocks.RUBBER_BLOCK), KlaxonBlocks.MOLTEN_RUBBER_BLOCK);
        addRubberMeltingRecipe(Ingredient.ofItems(KlaxonBlocks.RUBBER_SHEET_BLOCK), KlaxonBlocks.MOLTEN_RUBBER_BLOCK);
    }

    private void buildCookingRecipes() {
        addOreProcessingCookingRecipe(Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_MIXTURE), new ItemStack(KlaxonItems.CRUDE_STEEL_INGOT), 1.0f, 150, null, null);
    }

    private void addRubberMeltingRecipe(Ingredient ingredient, ItemConvertible output) {
        addBlastingSmeltingRecipe(ingredient, new ItemStack(output), 1.0f, 200, null, null);
    }
}
