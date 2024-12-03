package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// structure for this kinda yoinked from energized power
public class KlaxonRecipeProvider extends FabricRecipeProvider {
    private Map<RecipeType<?>, ArrayList<Identifier>> spentRecipeIdentifiersByRecipeType;

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
        buildMaterialCraftingRecipes(exporter);
        buildCompressionCraftingRecipes(exporter);
    }

    private void buildMaterialCraftingRecipes(RecipeExporter exporter) {
        addShapelessCraftingRecipe(exporter,
                DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.ofItems(KlaxonItems.FRACTURED_RAW_IRON),
                        Ingredient.ofItems(KlaxonItems.FRACTURED_RAW_IRON),
                        Ingredient.fromTag(KlaxonTags.Items.COAL),
                        Ingredient.fromTag(KlaxonTags.Items.COAL)),
                new ItemStack(KlaxonItems.CRUDE_STEEL_MIXTURE, 2),
                null, null);
    }

    private void buildCompressionCraftingRecipes(RecipeExporter exporter) {
        add3x3PackingRecipe(exporter, Ingredient.ofItems(KlaxonItems.STEEL_INGOT), new ItemStack(KlaxonBlocks.STEEL_BLOCK), null, null);
        add3x3PackingRecipe(exporter, Ingredient.ofItems(KlaxonItems.STEEL_NUGGET), new ItemStack(KlaxonItems.STEEL_INGOT), null, null);
        add3x3UnpackingRecipe(exporter, Ingredient.ofItems(KlaxonBlocks.STEEL_BLOCK), KlaxonItems.STEEL_INGOT, null, null);
        add3x3UnpackingRecipe(exporter, Ingredient.ofItems(KlaxonItems.STEEL_INGOT), KlaxonItems.STEEL_NUGGET, null, null);

        add2x2PackingRecipe(exporter, Ingredient.ofItems(KlaxonItems.FRACTURED_RAW_COPPER), new ItemStack(Items.RAW_COPPER), null, null);
        add2x2PackingRecipe(exporter, Ingredient.ofItems(KlaxonItems.FRACTURED_RAW_IRON), new ItemStack(Items.RAW_IRON), null, null);
        add2x2PackingRecipe(exporter, Ingredient.ofItems(KlaxonItems.FRACTURED_RAW_GOLD), new ItemStack(Items.RAW_GOLD), null, null);
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

    private void buildEquipmentCraftingRecipes(RecipeExporter exporter) {
        addShapedCraftingRecipe(exporter, Map.of(
                'B', Ingredient.fromTag(KlaxonTags.Items.STEEL_BLOCKS),
                'I', Ingredient.fromTag(KlaxonTags.Items.STEEL_INGOTS),
                'S', Ingredient.ofItems(Items.STICK),
                'L', Ingredient.ofItems(Items.LEATHER)),
                new String[]{
                        "BIB",
                        " S ",
                        "LSL"
                },
                new ItemStack(KlaxonItems.HAMMER),
                CraftingRecipeCategory.EQUIPMENT,
                null
        );
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

    private void add3x3UnpackingRecipe(RecipeExporter exporter,
                                     Ingredient input, ItemConvertible output,
                                     @Nullable CraftingRecipeCategory category, @Nullable String group) {
        addShapelessCraftingRecipe(exporter, DefaultedList.copyOf(Ingredient.EMPTY, input), new ItemStack(output, 9), category, group);
    }

    private void add3x3PackingRecipe(RecipeExporter exporter,
                                       Ingredient input, ItemStack output,
                                       @Nullable CraftingRecipeCategory category, @Nullable String group,
                                       final ResourceCondition... conditions) {
        String[] pattern = {
                "xxx",
                "xxx",
                "xxx"
        };

        addShapedCraftingRecipe(exporter, Map.of('x', input), pattern, output, category, group, conditions);
    }

    private void add2x2UnpackingRecipe(RecipeExporter exporter,
                                       Ingredient input, ItemConvertible output,
                                       @Nullable CraftingRecipeCategory category, @Nullable String group) {
        addShapelessCraftingRecipe(exporter, DefaultedList.copyOf(Ingredient.EMPTY, input), new ItemStack(output, 4), category, group);
    }

    private void add2x2PackingRecipe(RecipeExporter exporter,
                                     Ingredient input, ItemStack output,
                                     @Nullable CraftingRecipeCategory category, @Nullable String group,
                                     final ResourceCondition... conditions) {
        String[] pattern = {
                "xx",
                "xx"
        };

        addShapedCraftingRecipe(exporter, Map.of('x', input), pattern, output, category, group, conditions);
    }

    private void addShapelessCraftingRecipe(RecipeExporter exporter,
                                            DefaultedList<Ingredient> input, ItemStack output,
                                            @Nullable CraftingRecipeCategory category, @Nullable String group,
                                            final ResourceCondition... conditions) {
        Identifier recipeId = computeRecipeIdentifier(Registries.RECIPE_TYPE.getId(RecipeType.CRAFTING).getPath(),
                getItemPath(output.getItem()) + "_from_" + getItemPath(Arrays.stream(input.getFirst().getMatchingStacks()).findFirst().get().getItem()),
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = getItemPath(output.getItem());
        }

        ShapelessRecipe recipe = new ShapelessRecipe(group, category, output, input);

        acceptRecipeWithConditions(exporter, recipeId, recipe, null, conditions);
    }


    private void addShapedCraftingRecipe(RecipeExporter exporter,
                                         Map<Character, Ingredient> key, String[] pattern, ItemStack output,
                                         @Nullable CraftingRecipeCategory category, @Nullable String group,
                                         final ResourceCondition... conditions) {

        Identifier recipeId = computeRecipeIdentifier(Registries.RECIPE_TYPE.getId(RecipeType.CRAFTING).getPath(),
                getItemPath(output.getItem()),
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = getItemPath(output.getItem());
        }

        ShapedRecipe recipe = new ShapedRecipe(group, category, RawShapedRecipe.create(key, Arrays.stream(pattern).toList()), output);

        acceptRecipeWithConditions(exporter, recipeId, recipe, null, conditions);
    }

    private void addItemExplosionPowerRecipe(RecipeExporter exporter, Ingredient input,
                                             double explosionPower, boolean producesFire, final ResourceCondition... conditions) {

        Identifier recipeId = computeRecipeIdentifier(KlaxonRecipeTypes.ITEM_EXPLOSION_POWER_RECIPE_ID,
                Registries.ITEM.getId(input.getMatchingStacks()[0].getItem()).getPath(),
                conditions);

        ItemExplosionPowerRecipe recipe = new ItemExplosionPowerRecipe(input, explosionPower, producesFire);

        acceptRecipeWithConditions(exporter, recipeId, recipe, null, conditions);
    }

    private void addHammeringRecipe(RecipeExporter exporter, Ingredient input, ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = computeRecipeIdentifier(KlaxonRecipeTypes.HAMMERING_RECIPE_ID,
                getItemPath(output.getItem()),
                conditions);

        HammeringRecipe recipe = new HammeringRecipe(input, output);

        acceptRecipeWithConditions(exporter, recipeId, recipe, null, conditions);
    }

    private void addBlastProcessingRecipe(RecipeExporter exporter, Ingredient input,
                                                 double explosionPowerMin, double explosionPowerMax,
                                                 ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = computeRecipeIdentifier(KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID,
                getItemPath(output.getItem()),
                conditions);

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
        // get all the spent identifiers for the recipe type
        if (spentRecipeIdentifiersByRecipeType.containsKey(recipe.getType())) {
            // iterate through them all to check if theyre the same as the active recipe's id
            for (Identifier potentiallySpentIdentifier : spentRecipeIdentifiersByRecipeType.get(recipe.getType())) {
                // if there is a match, attach a discriminator to the end of the recipe id
                if (potentiallySpentIdentifier.equals(recipeId)) {
                    recipeId = recipeId.withPath(recipeId.getPath() + "_from_" + getItemPath(Arrays.stream(recipe.getIngredients().getFirst().getMatchingStacks()).findFirst().get().getItem()));
                    KlaxonCommon.LOGGER.info("Accommodated for duplicate recipe ID " + recipeId);
                }
            }
        } else {
            // if this recipe type hasn't had any recipes registered, add it to the map with a fresh arraylist
            spentRecipeIdentifiersByRecipeType.put(recipe.getType(), new ArrayList<Identifier>());
        }

        // add the new recipe id to the map for that recipe type
        spentRecipeIdentifiersByRecipeType.get(recipe.getType()).add(recipeId);

        // if the recipe has resource conditions, apply them
        if (conditions.length > 0) {
            withConditions(exporter, conditions).accept(recipeId, recipe, advancement);
        } else {
            exporter.accept(recipeId, recipe, advancement);
        }
    }
}
