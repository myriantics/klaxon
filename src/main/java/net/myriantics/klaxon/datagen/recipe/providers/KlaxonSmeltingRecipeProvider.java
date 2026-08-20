package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonSmeltingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonSmeltingRecipeProvider(KlaxonRecipeProvider provider, RecipeOutput exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildRecyclingRecipes();
        buildCookingRecipes();
    }

    private void buildRecyclingRecipes() {
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItemTags.STEEL_NUGGET_COOKING_RECYCLABLES), new ItemStack(KlaxonItems.STEEL_NUGGET), 3.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItemTags.STEEL_INGOT_COOKING_RECYCLABLES), new ItemStack(KlaxonItems.STEEL_INGOT), 3.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.IRON_WIRE.value()), new ItemStack(Items.IRON_NUGGET), 1.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.IRON_PLATE.value()), new ItemStack(Items.IRON_NUGGET), 1.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.GOLD_WIRE.value()), new ItemStack(Items.GOLD_NUGGET), 1.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.GOLD_PLATE.value()), new ItemStack(Items.GOLD_NUGGET), 1.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.COPPER_WIRE.value()), new ItemStack(KlaxonItems.COPPER_NUGGET), 1.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.COPPER_PLATE.value()), new ItemStack(KlaxonItems.COPPER_NUGGET), 1.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.STEEL_WIRE.value()), new ItemStack(KlaxonItems.STEEL_NUGGET), 1.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.STEEL_PLATE.value()), new ItemStack(KlaxonItems.STEEL_NUGGET), 1.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.CRUDE_STEEL_PLATE.value()), new ItemStack(KlaxonItems.STEEL_NUGGET), 1.0f, null, null);
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.CRUDE_STEEL_INGOT.value()), new ItemStack(KlaxonItems.STEEL_NUGGET), 1.0f, null, null);
    }

    private void buildCookingRecipes() {
        addBlastingAndSmeltingRecipe(Ingredient.of(KlaxonItems.CRUDE_STEEL_MIXTURE.value()), new ItemStack(KlaxonItems.CRUDE_STEEL_INGOT), 1.0f, null, null);

        addFoodProcessingCookingRecipe(Ingredient.of(KlaxonItems.HALLNOX_SLICE.value()), new ItemStack(KlaxonItems.DRIED_HALLNOX_SLICE), 0.2f, 200, null);
    }
}