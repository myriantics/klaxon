package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.item.KlaxonItems;

import java.util.List;

public class KlaxonHammeringRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonHammeringRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(RecipeExporter exporter) {
        buildHammeringRecipes(exporter);
    }

    private void buildHammeringRecipes(RecipeExporter exporter) {
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 4));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.SNOWBALL), new ItemStack(Items.SNOW));

        addHammeringRecipe(exporter, Ingredient.ofItems(KlaxonItems.STEEL_INGOT), new ItemStack(KlaxonItems.STEEL_PLATE));
        addHammeringRecipe(exporter, Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_INGOT), new ItemStack(KlaxonItems.CRUDE_STEEL_PLATE));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.IRON_INGOT), new ItemStack(KlaxonItems.IRON_PLATE));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.GOLD_INGOT), new ItemStack(KlaxonItems.GOLD_PLATE));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.COPPER_INGOT), new ItemStack(KlaxonItems.COPPER_PLATE));

        // create compat recipes - done manually for now because of issues with itemstacks returning air as an id - should resolve in future
        addHammeringRecipe(exporter, Ingredient.ofItems(KlaxonDatagenPhantomItems.CREATE_BRASS_INGOT), new ItemStack(KlaxonDatagenPhantomItems.CREATE_BRASS_SHEET),
                new AllModsLoadedResourceCondition(List.of(KlaxonDatagenPhantomItems.CREATE_MOD_ID)));
    }
}
