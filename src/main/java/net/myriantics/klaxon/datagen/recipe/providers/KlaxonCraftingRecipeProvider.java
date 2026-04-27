package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Map;

public class KlaxonCraftingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonCraftingRecipeProvider(KlaxonRecipeProvider provider, RecipeOutput exporter) {
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
        buildEquipmentCraftingRecipes();
    }

    private void buildEquipmentCraftingRecipes() {
        addGrappleClawRecipe(
                Ingredient.of(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES),
                Ingredient.of(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS),
                new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW, 1)
        );
        addGrappleClawRecipe(
                Ingredient.of(KlaxonConventionalItemTags.STEEL_PLATES),
                Ingredient.of(KlaxonConventionalItemTags.STEEL_INGOTS),
                new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW, 4)
        );
        addGrappleWinchRecipe(
                Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_CASING),
                Ingredient.of(KlaxonItems.STEEL_WIRE_SPOOL_BLOCK.value()),
                Ingredient.of(KlaxonItemTags.GEAR_GRIP_MATERIALS),
                new ItemStack(KlaxonItems.GRAPPLE_WINCH)
        );
    }

    private void buildWoodCraftingRecipes() {
        add2x2PackingRecipe(Ingredient.of(KlaxonItems.HALLNOX_STEM.value()), new ItemStack(KlaxonItems.HALLNOX_HYPHAE, 3), CraftingBookCategory.BUILDING, null);
        add2x2PackingRecipe(Ingredient.of(KlaxonItems.STRIPPED_HALLNOX_STEM.value()), new ItemStack(KlaxonItems.STRIPPED_HALLNOX_HYPHAE, 3), CraftingBookCategory.BUILDING, null);
        add2x2UnpackingRecipe(Ingredient.of(KlaxonItemTags.HALLNOX_STEMS), KlaxonItems.HALLNOX_PLANKS.value(), CraftingBookCategory.BUILDING, null);
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.HALLNOX_PLANKS.value())),
                new String[]{
                        "P  ",
                        "PP ",
                        "PPP"
                },
                new ItemStack(KlaxonItems.HALLNOX_STAIRS, 4),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.HALLNOX_PLANKS.value())),
                new String[]{
                        "PPP"
                },
                new ItemStack(KlaxonItems.HALLNOX_SLAB, 6),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.HALLNOX_PLANKS.value())),
                new String[]{
                        "PP"
                },
                new ItemStack(KlaxonItems.HALLNOX_PRESSURE_PLATE),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.HALLNOX_PLANKS.value())),
                new String[]{
                        "PP",
                        "PP",
                        "PP"
                },
                new ItemStack(KlaxonItems.HALLNOX_DOOR, 3),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.HALLNOX_PLANKS.value())),
                new String[]{
                        "PPP",
                        "PPP",
                },
                new ItemStack(KlaxonItems.HALLNOX_TRAPDOOR, 2),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.HALLNOX_PLANKS.value()),
                        'S', Ingredient.of(Items.STICK)),
                new String[]{
                        "PSP",
                        "PSP",
                },
                new ItemStack(KlaxonItems.HALLNOX_FENCE, 3),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.HALLNOX_PLANKS.value()),
                        'S', Ingredient.of(Items.STICK)),
                new String[]{
                        "SPS",
                        "SPS",
                },
                new ItemStack(KlaxonItems.HALLNOX_FENCE_GATE),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.HALLNOX_PLANKS.value()),
                        'S', Ingredient.of(Items.STICK)),
                new String[]{
                        "PPP",
                        "PPP",
                        " S "
                },
                new ItemStack(KlaxonItems.HALLNOX_SIGN, 3),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'S', Ingredient.of(KlaxonItems.STRIPPED_HALLNOX_STEM.value()),
                        'C', Ingredient.of(Items.CHAIN)),
                new String[]{
                        "C C",
                        "SSS",
                        "SSS"
                },
                new ItemStack(KlaxonItems.HALLNOX_HANGING_SIGN, 6),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapelessCraftingRecipe(
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(KlaxonItems.HALLNOX_PLANKS.value())),
                new ItemStack(KlaxonItems.HALLNOX_BUTTON),
                CraftingBookCategory.REDSTONE,
                null
        );
    }

    private void buildMachineCraftingRecipes() {
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(Items.POLISHED_DEEPSLATE),
                        'D', Ingredient.of(Items.DISPENSER)),
                new String[]{
                        "PPP",
                        "PDP",
                        "PPP"
                },
                new ItemStack(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR),
                CraftingBookCategory.REDSTONE,
                "blast_processors"
        );

        addShapedCraftingRecipe(Map.of(
                'P', Ingredient.of(KlaxonItems.CRUDE_STEEL_PLATE.value()),
                'I', Ingredient.of(KlaxonItems.CRUDE_STEEL_INGOT.value())),
                new String[]{
                        "IPI",
                        "P P",
                        "IPI"
                },
                new ItemStack(KlaxonItems.CRUDE_STEEL_CASING),
                CraftingBookCategory.REDSTONE,
                "steel_casing"
        );

        addShapedCraftingRecipe(Map.of(
                'P', Ingredient.of(KlaxonItems.STEEL_PLATE.value()),
                'I', Ingredient.of(KlaxonItems.STEEL_INGOT.value())),
                new String[]{
                        "IPI",
                        "P P",
                        "IPI"
                },
                new ItemStack(KlaxonItems.STEEL_CASING),
                CraftingBookCategory.REDSTONE,
                "steel_casing"
        );

        addPrecisionDispenserRecipe(KlaxonItems.STEEL_HELMET);
        addPrecisionDispenserRecipe(KlaxonItems.CRESTED_STEEL_HELMET);

        /*
        addShapedCraftingRecipe(Map.of(
                'R', Ingredient.ofItems(KlaxonItems.RUBBER_SHEET),
                'C', Ingredient.fromTag(KlaxonConventionalItemTags.COPPER_PLATES)),
                new String[] {
                        "CCC",
                        "CRC",
                        "CCC"
                },
                new ItemStack(KlaxonItems.COPPER_PIPE_MATRIX, 4),
                CraftingRecipeCategory.REDSTONE,
                "pipe_matrix"
        );
        */
    }

    private void buildDecorationCraftingRecipes() {
        // chains
        addShapedCraftingRecipe(Map.of(
                'I', Ingredient.of(KlaxonConventionalItemTags.STEEL_INGOTS),
                'N', Ingredient.of(KlaxonConventionalItemTags.STEEL_NUGGETS)),
                new String[] {
                        "N",
                        "I",
                        "N"
                },
                new ItemStack(Items.CHAIN, 12),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                'I', Ingredient.of(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS),
                'N', Ingredient.of(KlaxonConventionalItemTags.CRUDE_STEEL_NUGGETS)),
                new String[] {
                        "N",
                        "I",
                        "N"
                },
                new ItemStack(Items.CHAIN, 6),
                CraftingBookCategory.BUILDING,
                null
        );
        addShapedCraftingRecipe(Map.of(
                'H', Ingredient.of(KlaxonItems.HALLNOX_POD.value()),
                'P', Ingredient.of(KlaxonConventionalItemTags.PLATES),
                'G', Ingredient.of(ConventionalItemTags.GLASS_BLOCKS)),
                new String[] {
                        "PGP",
                        "GHG",
                        "PGP"
                },
                new ItemStack(KlaxonItems.HALLNOX_BULB, 4),
                CraftingBookCategory.BUILDING,
                null
        );

        addWaxingRecipe(KlaxonItems.COPPER_PLATING_BLOCK, KlaxonItems.WAXED_COPPER_PLATING_BLOCK);
        addWaxingRecipe(KlaxonItems.EXPOSED_COPPER_PLATING_BLOCK, KlaxonItems.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
        addWaxingRecipe(KlaxonItems.WEATHERED_COPPER_PLATING_BLOCK, KlaxonItems.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
        addWaxingRecipe(KlaxonItems.OXIDIZED_COPPER_PLATING_BLOCK, KlaxonItems.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);
        addWaxingRecipe(KlaxonItems.COPPER_WIRE_SPOOL_BLOCK, KlaxonItems.WAXED_COPPER_WIRE_SPOOL_BLOCK);
        addWaxingRecipe(KlaxonItems.EXPOSED_COPPER_WIRE_SPOOL_BLOCK, KlaxonItems.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
        addWaxingRecipe(KlaxonItems.WEATHERED_COPPER_WIRE_SPOOL_BLOCK, KlaxonItems.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
        addWaxingRecipe(KlaxonItems.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK, KlaxonItems.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);
        addWaxingRecipe(KlaxonItems.COPPER_PIPE_MATRIX, KlaxonItems.WAXED_COPPER_PIPE_MATRIX);
        addWaxingRecipe(KlaxonItems.EXPOSED_COPPER_PIPE_MATRIX, KlaxonItems.WAXED_EXPOSED_COPPER_PIPE_MATRIX);
        addWaxingRecipe(KlaxonItems.WEATHERED_COPPER_PIPE_MATRIX, KlaxonItems.WAXED_WEATHERED_COPPER_PIPE_MATRIX);
        addWaxingRecipe(KlaxonItems.OXIDIZED_COPPER_PIPE_MATRIX, KlaxonItems.WAXED_OXIDIZED_COPPER_PIPE_MATRIX);
    }

    private void buildRedstoneCraftingRecipes() {
        // steel doors / trapdoors
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonConventionalItemTags.STEEL_PLATES),
                        'I', Ingredient.of(KlaxonConventionalItemTags.STEEL_INGOTS)),
                new String[] {
                        "PP",
                        "PP",
                        "II"
                },
                new ItemStack(KlaxonItems.STEEL_DOOR, 3),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES),
                        'I', Ingredient.of(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS)),
                new String[] {
                        "PP",
                        "PP",
                        "II"
                },
                new ItemStack(KlaxonItems.CRUDE_STEEL_DOOR, 3),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonConventionalItemTags.STEEL_PLATES),
                        'I', Ingredient.of(KlaxonConventionalItemTags.STEEL_INGOTS)),
                new String[] {
                        "PP",
                        "II"
                },
                new ItemStack(KlaxonItems.STEEL_TRAPDOOR, 2),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonConventionalItemTags.CRUDE_STEEL_PLATES),
                        'I', Ingredient.of(KlaxonConventionalItemTags.CRUDE_STEEL_INGOTS)),
                new String[] {
                        "PP",
                        "II"
                },
                new ItemStack(KlaxonItems.CRUDE_STEEL_TRAPDOOR, 2),
                CraftingBookCategory.REDSTONE,
                null
        );

        // more efficient copper bulbs
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.COPPER_PLATING_BLOCK.value()),
                        'B', Ingredient.of(Items.BLAZE_ROD),
                        'R', Ingredient.of(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.COPPER_BULB, 4),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.EXPOSED_COPPER_PLATING_BLOCK.value()),
                        'B', Ingredient.of(Items.BLAZE_ROD),
                        'R', Ingredient.of(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.EXPOSED_COPPER_BULB, 4),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.WEATHERED_COPPER_PLATING_BLOCK.value()),
                        'B', Ingredient.of(Items.BLAZE_ROD),
                        'R', Ingredient.of(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WEATHERED_COPPER_BULB, 4),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.OXIDIZED_COPPER_PLATING_BLOCK.value()),
                        'B', Ingredient.of(Items.BLAZE_ROD),
                        'R', Ingredient.of(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.OXIDIZED_COPPER_BULB, 4),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.WAXED_COPPER_PLATING_BLOCK.value()),
                        'B', Ingredient.of(Items.BLAZE_ROD),
                        'R', Ingredient.of(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WAXED_COPPER_BULB, 4),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.WAXED_EXPOSED_COPPER_PLATING_BLOCK.value()),
                        'B', Ingredient.of(Items.BLAZE_ROD),
                        'R', Ingredient.of(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WAXED_EXPOSED_COPPER_BULB, 4),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.WAXED_WEATHERED_COPPER_PLATING_BLOCK.value()),
                        'B', Ingredient.of(Items.BLAZE_ROD),
                        'R', Ingredient.of(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WAXED_WEATHERED_COPPER_BULB, 4),
                CraftingBookCategory.REDSTONE,
                null
        );
        addShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItems.WAXED_OXIDIZED_COPPER_PLATING_BLOCK.value()),
                        'B', Ingredient.of(Items.BLAZE_ROD),
                        'R', Ingredient.of(Items.REDSTONE)),
                new String[]{
                        " P ",
                        "PBP",
                        " R "
                },
                new ItemStack(Items.WAXED_OXIDIZED_COPPER_BULB, 4),
                CraftingBookCategory.REDSTONE,
                null
        );
    }

    private void buildMaterialCraftingRecipes() {
        addShapelessCraftingRecipe(NonNullList.of(Ingredient.EMPTY,
                        Ingredient.of(KlaxonItemTags.FRACTURED_IRON),
                        Ingredient.of(KlaxonItemTags.FRACTURED_IRON),
                        Ingredient.of(KlaxonItemTags.FRACTURED_IRON),
                        Ingredient.of(KlaxonItemTags.FRACTURED_COALS)),
                new ItemStack(KlaxonItems.CRUDE_STEEL_MIXTURE, 3),
                null, null);

        add2x2PackingRecipe(Ingredient.of(KlaxonItems.FRACTURED_COAL.value()), new ItemStack(Items.COAL), null, null);
        add2x2PackingRecipe(Ingredient.of(KlaxonItems.FRACTURED_CHARCOAL.value()), new ItemStack(Items.CHARCOAL), null, null);

        // these take fences in the center and don't give them back because i'm EVIL HAHAHAHAHA
        addWireSpoolRecipe(Ingredient.of(KlaxonItems.IRON_WIRE.value()), new ItemStack(KlaxonItems.IRON_WIRE_SPOOL_BLOCK));
        addWireSpoolRecipe(Ingredient.of(KlaxonItems.STEEL_WIRE.value()), new ItemStack(KlaxonItems.STEEL_WIRE_SPOOL_BLOCK));
        addWireSpoolRecipe(Ingredient.of(KlaxonItems.GOLD_WIRE.value()), new ItemStack(KlaxonItems.GOLD_WIRE_SPOOL_BLOCK));
        addWireSpoolRecipe(Ingredient.of(KlaxonItems.COPPER_WIRE.value()), new ItemStack(KlaxonItems.COPPER_WIRE_SPOOL_BLOCK));

        addWireSpoolUncraftingRecipe(KlaxonItems.STEEL_WIRE_SPOOL_BLOCK, KlaxonItems.STEEL_WIRE);
        addWireSpoolUncraftingRecipe(KlaxonItems.IRON_WIRE_SPOOL_BLOCK, KlaxonItems.IRON_WIRE);
        addWireSpoolUncraftingRecipe(KlaxonItems.GOLD_WIRE_SPOOL_BLOCK, KlaxonItems.GOLD_WIRE);
        addWireSpoolUncraftingRecipe(KlaxonItems.COPPER_WIRE_SPOOL_BLOCK, KlaxonItems.COPPER_WIRE);
        addWireSpoolUncraftingRecipe(KlaxonItems.WAXED_COPPER_WIRE_SPOOL_BLOCK, KlaxonItems.COPPER_WIRE);
    }

    private void buildCompressionCraftingRecipes() {
        // storage blocks
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(KlaxonItems.STEEL_NUGGET, KlaxonItems.STEEL_INGOT, KlaxonItems.STEEL_BLOCK);
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(KlaxonItems.CRUDE_STEEL_NUGGET, KlaxonItems.CRUDE_STEEL_INGOT, KlaxonItems.CRUDE_STEEL_BLOCK);
        add3x3CompressionDecompressionRecipes(KlaxonItems.RUBBER_GLOB, KlaxonItems.RUBBER_BLOCK);
        add3x3CompressionDecompressionRecipes(KlaxonItems.RUBBER_SHEET, KlaxonItems.RUBBER_SHEET_BLOCK);

        // ingot / nugget
        add3x3PackingRecipe(Ingredient.of(KlaxonConventionalItemTags.COPPER_NUGGETS), new ItemStack(Items.COPPER_INGOT), null, null);
        add3x3UnpackingRecipe(Ingredient.of(Items.COPPER_INGOT), KlaxonItems.COPPER_NUGGET.value(), null, null);

        // plating blocks
        add2x2CompressionDecompressionRecipes(KlaxonItems.STEEL_PLATE, KlaxonItems.STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.CRUDE_STEEL_PLATE, KlaxonItems.CRUDE_STEEL_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.IRON_PLATE, KlaxonItems.IRON_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.GOLD_PLATE, KlaxonItems.GOLD_PLATING_BLOCK);
        add2x2CompressionDecompressionRecipes(KlaxonItems.COPPER_PLATE, KlaxonItems.COPPER_PLATING_BLOCK);
        add2x2UnpackingRecipe(Ingredient.of(KlaxonItems.WAXED_COPPER_PLATING_BLOCK.value()), KlaxonItems.COPPER_PLATE.value(), null, null);
    }

    private void add3x3IngotNuggetBlockCompressionDecompressionRecipes(Holder<Item> tiny, Holder<Item> small, Holder<Item> large, ResourceCondition... conditions) {
        add3x3IngotNuggetBlockCompressionDecompressionRecipes(tiny.value(), small.value(), large.value(), conditions);
    }

    private void add3x3IngotNuggetBlockCompressionDecompressionRecipes(ItemLike tiny, ItemLike small, ItemLike large, ResourceCondition... conditions) {
        add3x3CompressionDecompressionRecipes(tiny, small);
        add3x3CompressionDecompressionRecipes(small, large);
    }

    private void add3x3CompressionDecompressionRecipes(Holder<Item> small, Holder<Item> large, ResourceCondition... conditions) {
        add3x3CompressionDecompressionRecipes(small.value(), large.value(), conditions);
    }

    private void add3x3CompressionDecompressionRecipes(ItemLike small, ItemLike large, ResourceCondition... conditions) {
        add3x3PackingRecipe(Ingredient.of(small), new ItemStack(large, 1), null, null, conditions);
        add3x3UnpackingRecipe(Ingredient.of(large), small, null, null, conditions);
    }

    private void add2x2CompressionDecompressionRecipes(Holder<Item> small, Holder<Item> large, ResourceCondition... conditions) {
        add2x2CompressionDecompressionRecipes(small.value(), large.value(), conditions);
    }

    private void add2x2CompressionDecompressionRecipes(ItemLike small, ItemLike large, ResourceCondition... conditions) {
        add2x2PackingRecipe(Ingredient.of(small), new ItemStack(large, 1), null, null, conditions);
        add2x2UnpackingRecipe(Ingredient.of(large), small, null, null, conditions);
    }

    private void addWaxingRecipe(Holder<Item> unwaxed, Holder<Item> waxed) {
        addWaxingRecipe(unwaxed.value(), waxed.value());
    }

    private void addWireSpoolUncraftingRecipe(Holder<Item> spool, Holder<Item> wire) {
        addWireSpoolUncraftingRecipe(Ingredient.of(spool.value()), wire);
    }

    private void addWireSpoolUncraftingRecipe(Ingredient spool, Holder<Item> wire) {
        addShapelessCraftingRecipe(
                spool,
                new ItemStack(wire, 8),
                CraftingBookCategory.MISC,
                null
        );
    }

    private void addWaxingRecipe(Item unwaxed, Item waxed) {
        addShapelessCraftingRecipe(
                NonNullList.of(
                        Ingredient.EMPTY,
                        Ingredient.of(unwaxed),
                        Ingredient.of(Items.HONEYCOMB)
                ),
                new ItemStack(waxed),
                CraftingBookCategory.BUILDING,
                "klaxon.waxing"
        );
    }

    private void addWireSpoolRecipe(Ingredient wire, ItemStack result, ResourceCondition... conditions) {
        addShapedCraftingRecipe(Map.of(
                        'W', wire,
                        'F', Ingredient.of(ConventionalItemTags.WOODEN_FENCES)
                ),
                new String[] {
                        "WWW",
                        "WFW",
                        "WWW"
                },
                result,
                CraftingBookCategory.MISC,
                "wire_spools",
                conditions
        );
    }

    private void addGrappleClawRecipe(Ingredient plate, Ingredient ingot, ItemStack result, ResourceCondition... conditions) {
        Map<Character, Ingredient> map = Map.of(
                'P', plate,
                'I', ingot
                // n
                // g
                // a
                // s
                // !
        );

        addShapedCraftingRecipe(
                map,
                new String[] {
                        " P ",
                        "PIP",
                        "IP "
                },
                result,
                CraftingBookCategory.EQUIPMENT,
                "grapple_claw",
                conditions
        );
    }
    private void addGrappleWinchRecipe(Ingredient plate, Ingredient casing, Ingredient spool, Ingredient grip, ItemStack result) {
        addShapedCraftingRecipe(Map.of(
                        'P', plate,
                        'R', grip,
                        'S', spool,
                        'C', casing
                ),
                new String[] {
                        "RS ",
                        "PC ",
                        "PR "
                },
                result,
                CraftingBookCategory.EQUIPMENT,
                "grapple_winch"
        );
    }

    private void addPrecisionDispenserRecipe(Holder<Item> helmet) {
        addShapedCraftingRecipe(Map.of(
                        'H', Ingredient.of(helmet.value()),
                        'W', Ingredient.of(KlaxonConventionalItemTags.STEEL_WIRES),
                        'C', Ingredient.of(KlaxonItems.STEEL_CASING.value()),
                        'T', Ingredient.of(Items.CLOCK)
                ),
                new String[] {
                        "HW ",
                        "WCW",
                        " WT"
                },
                new ItemStack(KlaxonItems.PRECISION_DISPENSER),
                CraftingBookCategory.REDSTONE,
                "dispenser"
        );
    }
}
