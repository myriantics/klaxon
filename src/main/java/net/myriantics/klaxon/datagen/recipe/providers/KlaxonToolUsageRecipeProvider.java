package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;

import java.util.List;

public class KlaxonToolUsageRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonToolUsageRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildHammeringRecipes();
        buildWirecuttingRecipes();
        buildShearingRecipes();
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

        // create compat recipes - done manually for now because of issues with itemstacks returning air as an id - should resolve in future
        addHammeringRecipe(Ingredient.ofItems(KlaxonDatagenPhantomItems.CREATE_BRASS_INGOT), new ItemStack(KlaxonDatagenPhantomItems.CREATE_BRASS_SHEET),
                new AllModsLoadedResourceCondition(List.of(KlaxonDatagenPhantomItems.CREATE_MOD_ID)));
    }

    private void buildWirecuttingRecipes() {
        addWirecuttingRecipe(Ingredient.ofItems(Blocks.IRON_BARS), new ItemStack(Blocks.CHAIN, 3));
    }

    private void buildShearingRecipes() {
        addShearingRecipe(Ingredient.fromTag(ItemTags.WOOL), new ItemStack(Items.STRING, 2));
        addShearingRecipe(Ingredient.fromTag(ItemTags.WOOL_CARPETS), new ItemStack(Items.STRING, 1));
    }
}
