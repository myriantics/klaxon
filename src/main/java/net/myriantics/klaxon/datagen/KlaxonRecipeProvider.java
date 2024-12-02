package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.compat.KlaxonCompat;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.hammer.HammeringRecipe;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.KlaxonTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// structure for this kinda yoinked from energized power
public class KlaxonRecipeProvider extends FabricRecipeProvider {
    public KlaxonRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        buildCraftingRecipes(exporter);
        buildItemExplosionPowerRecipes(exporter);
        buildHammeringRecipes(exporter);
        buildBlastProcessingRecipes(exporter);
    }

    private void buildCookingRecipes(RecipeExporter exporter) {

    }

    private void buildCraftingRecipes(RecipeExporter exporter) {
        buildMachineCraftingRecipes(exporter);
        buildEquipmentCraftingRecipes(exporter);
    }

    private void buildMachineCraftingRecipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)
                .pattern("ppp")
                .pattern("pdp")
                .pattern("ppp")
                .input('p', Blocks.POLISHED_DEEPSLATE)
                .input('d', Blocks.DISPENSER)
                .criterion(FabricRecipeProvider.hasItem(Blocks.POLISHED_DEEPSLATE),
                        FabricRecipeProvider.conditionsFromItem(Blocks.POLISHED_DEEPSLATE))
                .criterion(FabricRecipeProvider.hasItem(Blocks.DISPENSER),
                        FabricRecipeProvider.conditionsFromItem(Blocks.DISPENSER))
                .offerTo(exporter);
    }

    private void buildEquipmentCraftingRecipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, KlaxonItems.HAMMER)
                .pattern("bib")
                .pattern(" s ")
                .pattern("lsl")
                .input('b', KlaxonTags.Items.STEEL_BLOCKS)
                .input('i', KlaxonTags.Items.STEEL_INGOTS)
                .input('s', Items.STICK)
                .input('l', Items.LEATHER)
                .criterion(FabricRecipeProvider.hasItem(KlaxonItems.STEEL_INGOT),
                        FabricRecipeProvider.conditionsFromItem(KlaxonItems.STEEL_INGOT))
                .offerTo(exporter);
    }

    private void buildHammeringRecipes(RecipeExporter exporter) {
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.RAW_COPPER), new ItemStack(KlaxonItems.FRACTURED_RAW_COPPER));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.RAW_IRON), new ItemStack(KlaxonItems.FRACTURED_RAW_IRON));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.RAW_GOLD), new ItemStack(KlaxonItems.FRACTURED_RAW_GOLD));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.SNOWBALL), new ItemStack(Items.SNOW));

        // create compat recipes - done manually for now because of issues with itemstacks returning air as an id - should resolve in future
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.COPPER_INGOT), new ItemStack(KlaxonCompat.CREATE_COPPER_SHEET),
                new AllModsLoadedResourceCondition(List.of(KlaxonCompat.CREATE_MOD_ID)));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.IRON_INGOT), new ItemStack(KlaxonCompat.CREATE_IRON_SHEET),
                new AllModsLoadedResourceCondition(List.of(KlaxonCompat.CREATE_MOD_ID)));
        addHammeringRecipe(exporter, Ingredient.ofItems(Items.GOLD_INGOT), new ItemStack(KlaxonCompat.CREATE_GOLD_SHEET),
                new AllModsLoadedResourceCondition(List.of(KlaxonCompat.CREATE_MOD_ID)));
        addHammeringRecipe(exporter, Ingredient.ofItems(KlaxonCompat.CREATE_BRASS_INGOT), new ItemStack(KlaxonCompat.CREATE_BRASS_SHEET),
                new AllModsLoadedResourceCondition(List.of(KlaxonCompat.CREATE_MOD_ID)));
    }

    private void buildBlastProcessingRecipes(RecipeExporter exporter) {
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(Items.RAW_COPPER), 0.2, 0.5, new ItemStack(KlaxonItems.FRACTURED_RAW_COPPER));
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(Items.RAW_IRON), 0.3, 0.6, new ItemStack(KlaxonItems.FRACTURED_RAW_IRON));
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(Items.RAW_GOLD), 0.2, 0.5, new ItemStack(KlaxonItems.FRACTURED_RAW_GOLD));

        addBlastProcessingRecipe(exporter, Ingredient.ofItems(Items.DEEPSLATE_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_DEEPSLATE_BRICKS));
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(Items.DEEPSLATE_TILES), 0.1, 0.3, new ItemStack(Items.CRACKED_DEEPSLATE_TILES));
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(Items.NETHER_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_NETHER_BRICKS));
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(Items.POLISHED_BLACKSTONE_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS));
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(Items.STONE_BRICKS), 0.1, 0.3, new ItemStack(Items.STONE_BRICKS));

        // create compat
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(KlaxonCompat.CREATE_PRECISION_MECHANISM), 0.2, 0.4, new ItemStack(Items.CLOCK),
                new AllModsLoadedResourceCondition(List.of(KlaxonCompat.CREATE_MOD_ID)));
    }

    private void buildItemExplosionPowerRecipes(RecipeExporter exporter) {
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.DRAGON_BREATH), 1.5, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.END_CRYSTAL), 6.0, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.FIRE_CHARGE), 0.5, true);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.GUNPOWDER), 0.3, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.TNT), 4.0, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.TNT_MINECART), 5.0, false);

        // meme recipes
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.CREEPER_SPAWN_EGG), 3.0, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.ENDER_DRAGON_SPAWN_EGG), 20.0, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.WITHER_SPAWN_EGG), 10.0, false);
        addItemExplosionPowerRecipe(exporter, Ingredient.ofItems(Items.GHAST_SPAWN_EGG), 3.5, true);
    }

    private void add3x3PackingRecipe(RecipeExporter exporter, Ingredient input, ItemConvertible output) {

    }

    private void add3x3UnpackingRecipe(RecipeExporter exporter, Ingredient input, ItemConvertible output) {

    }

    private void add2x2UnpackingRecipe(RecipeExporter exporter, Ingredient input, ItemConvertible output) {

    }

    private void add2x2PackingRecipe(RecipeExporter exporter, Ingredient input, ItemConvertible un) {

    }

    private void addShapelessCraftingRecipe() {

    }

    /*
    private void addShapedCraftingRecipe(RecipeExporter exporter, Map<Character, Ingredient> key, String[] pattern,
                                         ItemStack result, CraftingRecipeCategory category, String group) {
        ShapedRecipeJsonBuilder.create();
    }*/

    private void addItemExplosionPowerRecipe(RecipeExporter exporter, Ingredient input,
                                             double explosionPower, boolean producesFire, final ResourceCondition... conditions) {

        Identifier recipeId = computeRecipeIdentifier(KlaxonRecipeTypes.ITEM_EXPLOSION_POWER_RECIPE_ID,
                Registries.ITEM.getId(input.getMatchingStacks()[0].getItem()).getPath(), conditions);

        ItemExplosionPowerRecipe recipe = new ItemExplosionPowerRecipe(input, explosionPower, producesFire);

        acceptRecipeWithConditions(exporter, recipeId, recipe, null, conditions);
    }

    private void addHammeringRecipe(RecipeExporter exporter, Ingredient input, ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = computeRecipeIdentifier(KlaxonRecipeTypes.HAMMERING_RECIPE_ID, getItemPath(output.getItem()), conditions);

        HammeringRecipe recipe = new HammeringRecipe(input, output);

        acceptRecipeWithConditions(exporter, recipeId, recipe, null, conditions);
    }

    private void addBlastProcessingRecipe(RecipeExporter exporter, Ingredient input,
                                                 double explosionPowerMin, double explosionPowerMax,
                                                 ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = computeRecipeIdentifier(KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID, getItemPath(output.getItem()), conditions);

        BlastProcessingRecipe recipe = new BlastProcessingRecipe(input, explosionPowerMin, explosionPowerMax, output);

        acceptRecipeWithConditions(exporter, recipeId, recipe, null, conditions);
    }

    private Identifier computeRecipeIdentifier(String typeId, String path, final ResourceCondition... conditions) {
        for (ResourceCondition condition : conditions) {
            if (condition instanceof AllModsLoadedResourceCondition allModsLoadedResourceCondition) {
                return KlaxonCommon.locate(typeId + "/" + allModsLoadedResourceCondition.modIds().getFirst() + "/" + path);
            }
        }

        return KlaxonCommon.locate(typeId + "/" + path);
    }

    private void acceptRecipeWithConditions(RecipeExporter exporter, Identifier recipeId, Recipe<?> recipe, @Nullable AdvancementEntry advancement, final ResourceCondition... conditions) {
        if (conditions.length > 0) {
            withConditions(exporter, conditions).accept(recipeId, recipe, advancement);
        } else {
            exporter.accept(recipeId, recipe, advancement);
        }
    }
}
