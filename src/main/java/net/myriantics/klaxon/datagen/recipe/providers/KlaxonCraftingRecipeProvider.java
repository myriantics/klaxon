package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.item.KlaxonItems;

import java.util.Map;

public class KlaxonCraftingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonCraftingRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(RecipeExporter exporter) {
        buildMachineCraftingRecipes(exporter);
        buildMaterialCraftingRecipes(exporter);
        buildCompressionCraftingRecipes(exporter);
    }

    private void buildMachineCraftingRecipes(RecipeExporter exporter) {
        addShapedCraftingRecipe(exporter, Map.of(
                        'P', Ingredient.ofItems(Blocks.POLISHED_DEEPSLATE),
                        'D', Ingredient.ofItems(Blocks.DISPENSER)),
                new String[]{
                        "PPP",
                        "PDP",
                        "PPP"
                },
                new ItemStack(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR),
                CraftingRecipeCategory.REDSTONE,
                null
        );
    }

    private void buildMaterialCraftingRecipes(RecipeExporter exporter) {
        addShapelessCraftingRecipe(exporter,
                DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.ofItems(KlaxonItems.FRACTURED_IRON_FRAGMENTS),
                        Ingredient.ofItems(KlaxonItems.FRACTURED_IRON_FRAGMENTS),
                        Ingredient.fromTag(ItemTags.COALS),
                        Ingredient.fromTag(ItemTags.COALS)),
                new ItemStack(KlaxonItems.CRUDE_STEEL_MIXTURE, 2),
                null, null);
    }

    private void buildCompressionCraftingRecipes(RecipeExporter exporter) {
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(exporter, KlaxonItems.STEEL_NUGGET, KlaxonItems.STEEL_INGOT, KlaxonBlocks.STEEL_BLOCK.asItem());
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(exporter, KlaxonItems.CRUDE_STEEL_NUGGET, KlaxonItems.CRUDE_STEEL_INGOT, KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem());
    }

    private void add3x3IngotNuggetBlockCompressionDecompressionRecipes(RecipeExporter exporter, Item tiny, Item small, Item large, ResourceCondition... conditions) {
        add3x3CompressionDecompressionRecipes(exporter, tiny, small);
        add3x3CompressionDecompressionRecipes(exporter, small, large);
    }

    private void add3x3CompressionDecompressionRecipes(RecipeExporter exporter, Item small, Item large, ResourceCondition... conditions) {
        add3x3PackingRecipe(exporter, Ingredient.ofItems(small), new ItemStack(large, 1), null, null, conditions);
        add3x3UnpackingRecipe(exporter, Ingredient.ofItems(large), small, null, null, conditions);
    }
}
