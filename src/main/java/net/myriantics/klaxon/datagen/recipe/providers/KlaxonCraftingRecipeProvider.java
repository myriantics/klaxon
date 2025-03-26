package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.registry.KlaxonBlocks;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;

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
        buildDecorationCraftingRecipes(exporter);
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

    private void buildDecorationCraftingRecipes(RecipeExporter exporter) {
        addShapedCraftingRecipe(exporter, Map.of(
                'P', Ingredient.fromTag(KlaxonConventionalItemTags.STEEL_PLATES),
                'I', Ingredient.fromTag(KlaxonConventionalItemTags.STEEL_INGOTS)),
                new String[] {
                        "PP",
                        "PP",
                        "II"
                },
                new ItemStack(KlaxonBlocks.STEEL_DOOR, 3),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(exporter, Map.of(
                'P', Ingredient.fromTag(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES),
                'I', Ingredient.fromTag(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS)),
                new String[] {
                        "PP",
                        "PP",
                        "II"
                },
                new ItemStack(KlaxonBlocks.CRUDE_STEEL_DOOR, 3),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(exporter, Map.of(
                'P', Ingredient.fromTag(KlaxonConventionalItemTags.STEEL_PLATES),
                'I', Ingredient.fromTag(KlaxonConventionalItemTags.STEEL_INGOTS)),
                new String[] {
                        "PP",
                        "II"
                },
                new ItemStack(KlaxonBlocks.STEEL_TRAPDOOR, 2),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(exporter, Map.of(
                'P', Ingredient.fromTag(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES),
                'I', Ingredient.fromTag(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS)),
                new String[] {
                        "PP",
                        "II"
                },
                new ItemStack(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR, 2),
                CraftingRecipeCategory.REDSTONE,
                null
        );
    }

    private void buildMaterialCraftingRecipes(RecipeExporter exporter) {
        addShapelessCraftingRecipe(exporter,
                DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.ofItems(KlaxonItems.FRACTURED_IRON_FRAGMENTS),
                        Ingredient.ofItems(KlaxonItems.FRACTURED_IRON_FRAGMENTS),
                        Ingredient.ofItems(KlaxonItems.FRACTURED_COAL_CHUNKS),
                        Ingredient.ofItems(KlaxonItems.FRACTURED_COAL_CHUNKS)),
                new ItemStack(KlaxonItems.CRUDE_STEEL_MIXTURE, 3),
                null, null);

        add2x2PackingRecipe(exporter, Ingredient.ofItems(KlaxonItems.FRACTURED_COAL_CHUNKS), new ItemStack(Items.COAL), null, null);
    }

    private void buildCompressionCraftingRecipes(RecipeExporter exporter) {
        // storage blocks
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(exporter, KlaxonItems.STEEL_NUGGET, KlaxonItems.STEEL_INGOT, KlaxonBlocks.STEEL_BLOCK.asItem());
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(exporter, KlaxonItems.CRUDE_STEEL_NUGGET, KlaxonItems.CRUDE_STEEL_INGOT, KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem());

        // plating blocks
        add2x2CompressionDecompressionRecipes(exporter, KlaxonItems.STEEL_PLATE, KlaxonBlocks.STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(exporter, KlaxonItems.CRUDE_STEEL_PLATE, KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(exporter, KlaxonItems.IRON_PLATE, KlaxonBlocks.IRON_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(exporter, KlaxonItems.GOLD_PLATE, KlaxonBlocks.GOLD_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(exporter, KlaxonItems.COPPER_PLATE, KlaxonBlocks.COPPER_PLATING_BLOCK);
    }

    private void add3x3IngotNuggetBlockCompressionDecompressionRecipes(RecipeExporter exporter, ItemConvertible tiny, ItemConvertible small, ItemConvertible large, ResourceCondition... conditions) {
        add3x3CompressionDecompressionRecipes(exporter, tiny, small);
        add3x3CompressionDecompressionRecipes(exporter, small, large);
    }

    private void add3x3CompressionDecompressionRecipes(RecipeExporter exporter, ItemConvertible small, ItemConvertible large, ResourceCondition... conditions) {
        add3x3PackingRecipe(exporter, Ingredient.ofItems(small), new ItemStack(large, 1), null, null, conditions);
        add3x3UnpackingRecipe(exporter, Ingredient.ofItems(large), small, null, null, conditions);
    }

    private void add2x2CompressionDecompressionRecipes(RecipeExporter exporter, ItemConvertible small, ItemConvertible large, ResourceCondition... conditions) {
        add2x2PackingRecipe(exporter, Ingredient.ofItems(small), new ItemStack(large, 1), null, null, conditions);
        add2x2UnpackingRecipe(exporter, Ingredient.ofItems(large), small, null, null, conditions);
    }
}
