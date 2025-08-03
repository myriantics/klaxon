package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;
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
        buildWoodCraftingRecipes();
    }

    private void buildWoodCraftingRecipes() {
        add2x2PackingRecipe(Ingredient.ofItems(KlaxonItems.HALLNOX_STEM), new ItemStack(KlaxonItems.HALLNOX_HYPHAE, 3), CraftingRecipeCategory.BUILDING, null);
        add2x2PackingRecipe(Ingredient.ofItems(KlaxonItems.STRIPPED_HALLNOX_STEM), new ItemStack(KlaxonItems.STRIPPED_HALLNOX_HYPHAE, 3), CraftingRecipeCategory.BUILDING, null);
        add2x2UnpackingRecipe(Ingredient.fromTag(KlaxonItemTags.HALLNOX_STEMS), KlaxonItems.HALLNOX_PLANKS, CraftingRecipeCategory.BUILDING, null);
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.HALLNOX_PLANKS)),
                new String[]{
                        "P  ",
                        "PP ",
                        "PPP"
                },
                new ItemStack(KlaxonItems.HALLNOX_STAIRS, 4),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.HALLNOX_PLANKS)),
                new String[]{
                        "PPP"
                },
                new ItemStack(KlaxonItems.HALLNOX_SLAB, 6),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.HALLNOX_PLANKS)),
                new String[]{
                        "PP"
                },
                new ItemStack(KlaxonItems.HALLNOX_PRESSURE_PLATE),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.HALLNOX_PLANKS)),
                new String[]{
                        "PP",
                        "PP",
                        "PP"
                },
                new ItemStack(KlaxonItems.HALLNOX_DOOR, 3),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.HALLNOX_PLANKS)),
                new String[]{
                        "PPP",
                        "PPP",
                },
                new ItemStack(KlaxonItems.HALLNOX_TRAPDOOR, 2),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.HALLNOX_PLANKS),
                        'S', Ingredient.ofItems(Items.STICK)),
                new String[]{
                        "PSP",
                        "PSP",
                },
                new ItemStack(KlaxonItems.HALLNOX_FENCE, 3),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.HALLNOX_PLANKS),
                        'S', Ingredient.ofItems(Items.STICK)),
                new String[]{
                        "SPS",
                        "SPS",
                },
                new ItemStack(KlaxonItems.HALLNOX_FENCE_GATE),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.HALLNOX_PLANKS),
                        'S', Ingredient.ofItems(Items.STICK)),
                new String[]{
                        "PPP",
                        "PPP",
                        " S "
                },
                new ItemStack(KlaxonItems.HALLNOX_SIGN),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'S', Ingredient.ofItems(KlaxonItems.STRIPPED_HALLNOX_STEM),
                        'C', Ingredient.ofItems(Items.CHAIN)),
                new String[]{
                        "C C",
                        "SSS",
                        "SSS"
                },
                new ItemStack(KlaxonItems.HALLNOX_HANGING_SIGN, 6),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapelessCraftingRecipe(
                DefaultedList.copyOf(Ingredient.EMPTY, Ingredient.ofItems(KlaxonItems.HALLNOX_PLANKS)),
                new ItemStack(KlaxonItems.HALLNOX_BUTTON),
                CraftingRecipeCategory.REDSTONE,
                null
        );
    }

    private void buildMachineCraftingRecipes() {
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(Items.POLISHED_DEEPSLATE),
                        'D', Ingredient.ofItems(Items.DISPENSER)),
                new String[]{
                        "PPP",
                        "PDP",
                        "PPP"
                },
                new ItemStack(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR),
                CraftingRecipeCategory.REDSTONE,
                "blast_processors"
        );

        addShapedCraftingRecipe(Map.of(
                '#', Ingredient.ofItems(Items.SMOOTH_STONE),
                'S', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                'X', Ingredient.ofItems(Items.FURNACE)),
                new String[]{
                        "SSS",
                        "SXS",
                        "###"
                },
                new ItemStack(Items.BLAST_FURNACE),
                CraftingRecipeCategory.REDSTONE,
                null
        );

        addShapedCraftingRecipe(Map.of(
                'P', Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_PLATE),
                'I', Ingredient.ofItems(KlaxonItems.CRUDE_STEEL_INGOT)),
                new String[]{
                        "IPI",
                        "P P",
                        "IPI"
                },
                new ItemStack(KlaxonItems.CRUDE_STEEL_CASING, 4),
                CraftingRecipeCategory.REDSTONE,
                "steel_casing"
        );

        addShapedCraftingRecipe(Map.of(
                'P', Ingredient.ofItems(KlaxonItems.STEEL_PLATE),
                'I', Ingredient.ofItems(KlaxonItems.STEEL_INGOT)),
                new String[]{
                        "IPI",
                        "P P",
                        "IPI"
                },
                new ItemStack(KlaxonItems.STEEL_CASING, 4),
                CraftingRecipeCategory.REDSTONE,
                "steel_casing"
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
                new ItemStack(Items.CHAIN, 12),
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
                new ItemStack(Items.CHAIN, 6),
                CraftingRecipeCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                'H', Ingredient.ofItems(KlaxonItems.HALLNOX_POD),
                'P', Ingredient.fromTag(KlaxonConventionalItemTags.PLATES),
                'G', Ingredient.fromTag(ConventionalItemTags.GLASS_BLOCKS)),
                new String[] {
                        "PGP",
                        "GHG",
                        "PGP"
                },
                new ItemStack(KlaxonItems.HALLNOX_BULB, 4),
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
                new ItemStack(KlaxonItems.STEEL_DOOR, 3),
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
                new ItemStack(KlaxonItems.CRUDE_STEEL_DOOR, 3),
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
                new ItemStack(KlaxonItems.STEEL_TRAPDOOR, 2),
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
                new ItemStack(KlaxonItems.CRUDE_STEEL_TRAPDOOR, 2),
                CraftingRecipeCategory.REDSTONE,
                null
        );

        // more efficient copper bulbs
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.EXPOSED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.EXPOSED_COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.WEATHERED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WEATHERED_COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.OXIDIZED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.OXIDIZED_COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.WAXED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WAXED_COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.WAXED_EXPOSED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WAXED_EXPOSED_COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.WAXED_WEATHERED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WAXED_WEATHERED_COPPER_BULB, 4),
                CraftingRecipeCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.ofItems(KlaxonItems.WAXED_OXIDIZED_COPPER_PLATING_BLOCK),
                        'B', Ingredient.ofItems(Items.BLAZE_ROD),
                        'R', Ingredient.ofItems(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WAXED_OXIDIZED_COPPER_BULB, 4),
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
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(KlaxonItems.STEEL_NUGGET, KlaxonItems.STEEL_INGOT, KlaxonItems.STEEL_BLOCK);
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(KlaxonItems.CRUDE_STEEL_NUGGET, KlaxonItems.CRUDE_STEEL_INGOT, KlaxonItems.CRUDE_STEEL_BLOCK);
        add3x3CompressionDecompressionRecipes(KlaxonItems.MOLTEN_RUBBER_GLOB, KlaxonItems.MOLTEN_RUBBER_BLOCK);
        add3x3PackingRecipe(Ingredient.ofItems(KlaxonItems.MOLTEN_RUBBER_SHEET), new ItemStack(KlaxonItems.MOLTEN_RUBBER_BLOCK), null, null);
        add3x3CompressionDecompressionRecipes(KlaxonItems.RUBBER_SHEET, KlaxonItems.RUBBER_SHEET_BLOCK);

        // plating blocks
        add2x2CompressionDecompressionRecipes(KlaxonItems.STEEL_PLATE, KlaxonItems.STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.CRUDE_STEEL_PLATE, KlaxonItems.CRUDE_STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.IRON_PLATE, KlaxonItems.IRON_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.GOLD_PLATE, KlaxonItems.GOLD_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.COPPER_PLATE, KlaxonItems.COPPER_PLATING_BLOCK);
        add2x2UnpackingRecipe(Ingredient.ofItems(KlaxonItems.WAXED_COPPER_PLATING_BLOCK), KlaxonItems.COPPER_PLATE, null, null);
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
