package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;

public class KlaxonToolUsageRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonToolUsageRecipeProvider(KlaxonRecipeProvider provider, RecipeOutput exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildHammeringRecipes();
        buildWirecuttingRecipes();
    }

    private void buildHammeringRecipes() {
        addHammeringRecipe(Ingredient.of(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 4));
        addHammeringRecipe(Ingredient.of(Items.SNOWBALL), new ItemStack(Items.SNOW));

        addHammeringRecipe(Ingredient.of(KlaxonItems.STEEL_INGOT.value()), new ItemStack(KlaxonItems.STEEL_PLATE));
        addHammeringRecipe(Ingredient.of(KlaxonItems.CRUDE_STEEL_INGOT.value()), new ItemStack(KlaxonItems.CRUDE_STEEL_PLATE));
        addHammeringRecipe(Ingredient.of(Items.IRON_INGOT), new ItemStack(KlaxonItems.IRON_PLATE));
        addHammeringRecipe(Ingredient.of(Items.GOLD_INGOT), new ItemStack(KlaxonItems.GOLD_PLATE));
        addHammeringRecipe(Ingredient.of(Items.COPPER_INGOT), new ItemStack(KlaxonItems.COPPER_PLATE));

        addHammeringRecipe(Ingredient.of(KlaxonItems.RUBBER_GLOB.value()), new ItemStack(KlaxonItems.RUBBER_SHEET));
    }

    private void buildWirecuttingRecipes() {
        addWirecuttingRecipe(Ingredient.of(Items.IRON_BARS), new ItemStack(Items.CHAIN, 2));
        addWirecuttingRecipe(Ingredient.of(KlaxonConventionalItemTags.STEEL_PLATES), new ItemStack(KlaxonItems.STEEL_WIRE, 3));
        addWirecuttingRecipe(Ingredient.of(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES), new ItemStack(KlaxonItems.STEEL_WIRE, 1));
        addWirecuttingRecipe(Ingredient.of(KlaxonConventionalItemTags.IRON_PLATES), new ItemStack(KlaxonItems.IRON_WIRE, 3));
        addWirecuttingRecipe(Ingredient.of(KlaxonConventionalItemTags.GOLD_PLATES), new ItemStack(KlaxonItems.GOLD_WIRE, 3));
        addWirecuttingRecipe(Ingredient.of(KlaxonConventionalItemTags.COPPER_PLATES), new ItemStack(KlaxonItems.COPPER_WIRE, 3));
    }
}
