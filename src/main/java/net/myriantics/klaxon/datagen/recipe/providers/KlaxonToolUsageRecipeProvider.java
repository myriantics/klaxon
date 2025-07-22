package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;

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
        addWirecuttingRecipe(Ingredient.ofItems(Items.IRON_BARS), new ItemStack(Items.CHAIN, 2));
    }

    private void buildShearingRecipes() {
        addShearingRecipe(Ingredient.ofItems(Items.WHITE_WOOL), new ItemStack(Items.WHITE_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.ORANGE_WOOL), new ItemStack(Items.ORANGE_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.MAGENTA_WOOL), new ItemStack(Items.MAGENTA_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.LIGHT_BLUE_WOOL), new ItemStack(Items.LIGHT_BLUE_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.YELLOW_WOOL), new ItemStack(Items.YELLOW_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.LIME_WOOL), new ItemStack(Items.LIME_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.PINK_WOOL), new ItemStack(Items.PINK_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.GRAY_WOOL), new ItemStack(Items.GRAY_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.LIGHT_GRAY_WOOL), new ItemStack(Items.LIGHT_GRAY_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.CYAN_WOOL), new ItemStack(Items.CYAN_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.PURPLE_WOOL), new ItemStack(Items.PURPLE_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.BLUE_WOOL), new ItemStack(Items.BLUE_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.BROWN_WOOL), new ItemStack(Items.BROWN_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.GREEN_WOOL), new ItemStack(Items.GREEN_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.RED_WOOL), new ItemStack(Items.RED_CARPET, 6));
        addShearingRecipe(Ingredient.ofItems(Items.BLACK_WOOL), new ItemStack(Items.BLACK_CARPET, 6));
    }
}
