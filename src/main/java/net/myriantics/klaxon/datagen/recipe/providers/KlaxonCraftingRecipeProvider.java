package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
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
import java.util.function.Consumer;

public class KlaxonCraftingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonCraftingRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> consumer) {
        buildMachineCraftingRecipes(consumer);
        buildMaterialCraftingRecipes(consumer);
        buildCompressionCraftingRecipes(consumer);
        buildDecorationCraftingRecipes(consumer);
    }

    private void buildMachineCraftingRecipes(Consumer<RecipeJsonProvider> consumer) {
        addShapedCraftingRecipe(consumer, Map.of(
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

    private void buildDecorationCraftingRecipes(Consumer<RecipeJsonProvider> consumer) {
        addShapedCraftingRecipe(consumer, Map.of(
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
        addShapedCraftingRecipe(consumer, Map.of(
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
        addShapedCraftingRecipe(consumer, Map.of(
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
        addShapedCraftingRecipe(consumer, Map.of(
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

    private void buildMaterialCraftingRecipes(Consumer<RecipeJsonProvider> consumer) {
        addShapelessCraftingRecipe(consumer,
                DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.ofItems(KlaxonItems.FRACTURED_IRON_FRAGMENTS),
                        Ingredient.ofItems(KlaxonItems.FRACTURED_IRON_FRAGMENTS),
                        Ingredient.ofItems(KlaxonItems.FRACTURED_COAL_CHUNKS),
                        Ingredient.ofItems(KlaxonItems.FRACTURED_COAL_CHUNKS)),
                new ItemStack(KlaxonItems.CRUDE_STEEL_MIXTURE, 3),
                null, null);
    }

    private void buildCompressionCraftingRecipes(Consumer<RecipeJsonProvider> consumer) {
        // storage blocks
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(consumer, KlaxonItems.STEEL_NUGGET, KlaxonItems.STEEL_INGOT, KlaxonBlocks.STEEL_BLOCK.asItem());
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(consumer, KlaxonItems.CRUDE_STEEL_NUGGET, KlaxonItems.CRUDE_STEEL_INGOT, KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem());

        // plating blocks
        add2x2CompressionDecompressionRecipes(consumer, KlaxonItems.STEEL_PLATE, KlaxonBlocks.STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(consumer, KlaxonItems.CRUDE_STEEL_PLATE, KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(consumer, KlaxonItems.IRON_PLATE, KlaxonBlocks.IRON_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(consumer, KlaxonItems.GOLD_PLATE, KlaxonBlocks.GOLD_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(consumer, KlaxonItems.COPPER_PLATE, KlaxonBlocks.COPPER_PLATING_BLOCK);
    }

    private void add3x3IngotNuggetBlockCompressionDecompressionRecipes(Consumer<RecipeJsonProvider> consumer, ItemConvertible tiny, ItemConvertible small, ItemConvertible large, ConditionJsonProvider... conditions) {
        add3x3CompressionDecompressionRecipes(consumer, tiny, small);
        add3x3CompressionDecompressionRecipes(consumer, small, large);
    }

    private void add3x3CompressionDecompressionRecipes(Consumer<RecipeJsonProvider> consumer, ItemConvertible small, ItemConvertible large, ConditionJsonProvider... conditions) {
        add3x3PackingRecipe(consumer, Ingredient.ofItems(small), new ItemStack(large, 1), null, null, conditions);
        add3x3UnpackingRecipe(consumer, Ingredient.ofItems(large), small, null, null, conditions);
    }

    private void add2x2CompressionDecompressionRecipes(Consumer<RecipeJsonProvider> consumer, ItemConvertible small, ItemConvertible large, ConditionJsonProvider... conditions) {
        add2x2PackingRecipe(consumer, Ingredient.ofItems(small), new ItemStack(large, 1), null, null, conditions);
        add2x2UnpackingRecipe(consumer, Ingredient.ofItems(large), small, null, null, conditions);
    }
}
