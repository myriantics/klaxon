package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Map;

public class KlaxonCraftingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonCraftingRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildMachineCraftingRecipes();
        buildMaterialCraftingRecipes();
        buildCompressionCraftingRecipes();
        buildDecorationCraftingRecipes();
        buildRedstoneCraftingRecipes();
    }

    private void buildMachineCraftingRecipes() {
        addShapedCraftingRecipe(Map.of(
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

        addShapedCraftingRecipe(Map.of(
                '#', Ingredient.ofItems(Blocks.SMOOTH_STONE),
                'S', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                'X', Ingredient.ofItems(Blocks.FURNACE)),
                new String[]{
                        "SSS",
                        "SXS",
                        "###"
                },
                new ItemStack(Blocks.BLAST_FURNACE),
                CraftingRecipeCategory.REDSTONE,
                null
        );
    }

    private void buildDecorationCraftingRecipes() {
        // chains
        addShapedCraftingRecipe(Map.of(
                'I', Ingredient.fromTag(KlaxonConventionalItemTags.STEEL_INGOTS),
                'N', Ingredient.fromTag(KlaxonConventionalItemTags.STEEL_NUGGETS)),
                new String[] {
                        "N",
                        "I",
                        "N"
                },
                new ItemStack(Blocks.CHAIN, 12),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                'I', Ingredient.fromTag(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS),
                'N', Ingredient.fromTag(KlaxonConventionalItemTags.CRUDE_STEEL_NUGGETS)),
                new String[] {
                        "N",
                        "I",
                        "N"
                },
                new ItemStack(Blocks.CHAIN, 6),
                CraftingRecipeCategory.BUILDING,
                null
        );
    }

    private void buildRedstoneCraftingRecipes() {
        // steel doors / trapdoors
        addShapedCraftingRecipe(Map.of(
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
        addShapedCraftingRecipe(Map.of(
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
        addShapedCraftingRecipe(Map.of(
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
        addShapedCraftingRecipe(Map.of(
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

        // more efficient copper bulbs
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonBlocks.COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Blocks.COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Blocks.EXPOSED_COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Blocks.WEATHERED_COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Blocks.OXIDIZED_COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
    }

    private void buildMaterialCraftingRecipes() {
        addShapelessCraftingRecipe(DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.fromTag(KlaxonItemTags.FRACTURED_IRON),
                        Ingredient.fromTag(KlaxonItemTags.FRACTURED_IRON),
                        Ingredient.fromTag(KlaxonItemTags.FRACTURED_IRON),
                        Ingredient.fromTag(KlaxonItemTags.FRACTURED_COALS)),
                new ItemStack(KlaxonItems.CRUDE_STEEL_MIXTURE, 3),
                null, null);

        add2x2PackingRecipe(Ingredient.ofItems(KlaxonItems.FRACTURED_COAL), new ItemStack(Items.COAL), null, null);
        add2x2PackingRecipe(Ingredient.ofItems(KlaxonItems.FRACTURED_CHARCOAL), new ItemStack(Items.CHARCOAL), null, null);
    }

    private void buildCompressionCraftingRecipes() {
        // storage blocks
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(KlaxonItems.STEEL_NUGGET, KlaxonItems.STEEL_INGOT, KlaxonBlocks.STEEL_BLOCK.asItem());
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(KlaxonItems.CRUDE_STEEL_NUGGET, KlaxonItems.CRUDE_STEEL_INGOT, KlaxonBlocks.CRUDE_STEEL_BLOCK.asItem());
        add3x3CompressionDecompressionRecipes(KlaxonItems.MOLTEN_RUBBER_GLOB, KlaxonBlocks.MOLTEN_RUBBER_BLOCK);
        add3x3PackingRecipe(Ingredient.ofItems(KlaxonItems.MOLTEN_RUBBER_SHEET), new ItemStack(KlaxonBlocks.MOLTEN_RUBBER_BLOCK), null, null);
        add3x3CompressionDecompressionRecipes(KlaxonItems.RUBBER_SHEET, KlaxonBlocks.RUBBER_SHEET_BLOCK);

        // plating blocks
        add2x2CompressionDecompressionRecipes(KlaxonItems.STEEL_PLATE, KlaxonBlocks.STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.CRUDE_STEEL_PLATE, KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.IRON_PLATE, KlaxonBlocks.IRON_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.GOLD_PLATE, KlaxonBlocks.GOLD_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.COPPER_PLATE, KlaxonBlocks.COPPER_PLATING_BLOCK);
        add2x2UnpackingRecipe(Ingredient.ofItems(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK), KlaxonItems.COPPER_PLATE, null, null);
    }

    private void add3x3IngotNuggetBlockCompressionDecompressionRecipes(ItemConvertible tiny, ItemConvertible small, ItemConvertible large, ResourceCondition... conditions) {
        add3x3CompressionDecompressionRecipes(tiny, small);
        add3x3CompressionDecompressionRecipes(small, large);
    }

    private void add3x3CompressionDecompressionRecipes(ItemConvertible small, ItemConvertible large, ResourceCondition... conditions) {
        add3x3PackingRecipe(Ingredient.ofItems(small), new ItemStack(large, 1), null, null, conditions);
        add3x3UnpackingRecipe(Ingredient.ofItems(large), small, null, null, conditions);
    }

    private void add2x2CompressionDecompressionRecipes(ItemConvertible small, ItemConvertible large, ResourceCondition... conditions) {
        add2x2PackingRecipe(Ingredient.ofItems(small), new ItemStack(large, 1), null, null, conditions);
        add2x2UnpackingRecipe(Ingredient.ofItems(large), small, null, null, conditions);
    }
}
