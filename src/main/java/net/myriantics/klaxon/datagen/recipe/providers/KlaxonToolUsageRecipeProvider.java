package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.KlaxonDatagenCompatIds;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;

import java.util.List;

public class KlaxonToolUsageRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonToolUsageRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildHammeringRecipes();
        buildWirecuttingRecipes();
    }

    private void buildHammeringRecipes() {
        addHammeringRecipe(Ingredient.ofItems(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 4));
        addHammeringRecipe(Ingredient.ofItems(Items.SNOWBALL), new ItemStack(Items.SNOW));

        addHammeringRecipe(Ingredient.ofItems(KlaxonItems.STEEL_INGOT), new ItemStack(KlaxonItems.STEEL_PLATE));
        addHammeringRecipe(Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_INGOT), new ItemStack(KlaxonItems.CRUDE_STEEL_PLATE));
        addHammeringRecipe(Ingredient.ofItems(Items.IRON_INGOT), new ItemStack(KlaxonItems.IRON_PLATE));
        addHammeringRecipe(Ingredient.ofItems(Items.GOLD_INGOT), new ItemStack(KlaxonItems.GOLD_PLATE));
        addHammeringRecipe(Ingredient.ofItems(Items.COPPER_INGOT), new ItemStack(KlaxonItems.COPPER_PLATE));

        addHammeringRecipe(Ingredient.ofItems(KlaxonItems.MOLTEN_RUBBER_GLOB), new ItemStack(KlaxonItems.MOLTEN_RUBBER_SHEET));
    }

    private void buildWirecuttingRecipes() {
        addWirecuttingRecipe(Ingredient.ofItems(Items.IRON_BARS), new ItemStack(Items.CHAIN, 2));
        addWirecuttingRecipe(Ingredient.fromTag(KlaxonConventionalItemTags.STEEL_PLATES), new ItemStack(KlaxonItems.STEEL_WIRE, 3));
        addWirecuttingRecipe(Ingredient.fromTag(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES), new ItemStack(KlaxonItems.STEEL_WIRE, 1));
        addWirecuttingRecipe(Ingredient.fromTag(KlaxonConventionalItemTags.IRON_PLATES), new ItemStack(KlaxonItems.IRON_WIRE, 3));
        addWirecuttingRecipe(Ingredient.fromTag(KlaxonConventionalItemTags.GOLD_PLATES), new ItemStack(KlaxonItems.GOLD_WIRE, 3));
        addWirecuttingRecipe(Ingredient.fromTag(KlaxonConventionalItemTags.COPPER_PLATES), new ItemStack(KlaxonItems.COPPER_WIRE, 3));
    }
}
