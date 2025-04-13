package net.myriantics.klaxon.datagen.recipe;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.NamedIngredient;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipe;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipe;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shaped.MakeshiftShapedCraftingRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shapeless.MakeshiftShapelessCraftingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.minecraft.data.server.recipe.RecipeProvider.getItemPath;

// used to break down recipe datagen into multiple classes for easier management
public abstract class KlaxonRecipeSubProvider {

    public final KlaxonRecipeProvider provider;
    public final RecipeExporter exporter;

    public KlaxonRecipeSubProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        this.provider = provider;
        this.exporter = exporter;
    }

    public abstract void generateRecipes();

    // recipe adding code below (to be used by subclasses)

    public void add3x3UnpackingRecipe(Ingredient input, ItemConvertible output,
                                      @Nullable CraftingRecipeCategory category, @Nullable String group,
                                      final ResourceCondition... conditions) {
        addShapelessCraftingRecipe(DefaultedList.copyOf(input), new ItemStack(output, 9), category, group, conditions);
    }

    public void add3x3PackingRecipe(Ingredient input, ItemStack output,
                                    @Nullable CraftingRecipeCategory category, @Nullable String group,
                                    final ResourceCondition... conditions) {
        String[] pattern = {
                "xxx",
                "xxx",
                "xxx"
        };

        addShapedCraftingRecipe(Map.of('x', input), pattern, output, category, group, conditions);
    }

    public void add2x2UnpackingRecipe(Ingredient input, ItemConvertible output,
                                      @Nullable CraftingRecipeCategory category, @Nullable String group,
                                      final ResourceCondition... conditions) {
        addShapelessCraftingRecipe(DefaultedList.copyOf(input), new ItemStack(output, 4), category, group, conditions);
    }

    public void add2x2PackingRecipe(Ingredient input, ItemStack output,
                                    @Nullable CraftingRecipeCategory category, @Nullable String group,
                                    final ResourceCondition... conditions) {
        String[] pattern = {
                "xx",
                "xx"
        };

        addShapedCraftingRecipe(Map.of('x', input), pattern, output, category, group, conditions);
    }

    public void addFoodProcessingCookingRecipe(Ingredient input, ItemStack output,
                                               float experience, int cookingTime,
                                               @Nullable String group,
                                               final ResourceCondition... conditions) {
        addSmokingSmeltingRecipe(
                input, output, experience, (int) (cookingTime * 0.5),
                CookingRecipeCategory.FOOD, group, conditions);
        addSmeltingRecipe(input, output, experience, cookingTime, CookingRecipeCategory.FOOD, group, conditions);
    }

    public void addOreProcessingCookingRecipe(Ingredient input, ItemStack output,
                                              float experience, int cookingTime,
                                              @Nullable CookingRecipeCategory category, @Nullable String group,
                                              final ResourceCondition... conditions) {
        addBlastingSmeltingRecipe(
                input, output, experience, (int) (cookingTime * 0.5),
                category, group, conditions);
        addSmeltingRecipe(input, output, experience, cookingTime, category, group, conditions);
    }

    public void addSmeltingRecipe(Ingredient input, ItemStack output,
                                  float experience, int cookingTime,
                                  @Nullable CookingRecipeCategory category, @Nullable String group,
                                  final ResourceCondition... conditions) {
        String outputPath = getItemPath(output.getItem());

        Identifier recipeId = provider.computeRecipeIdentifier("cooking/smelting",
                outputPath,
                conditions
        );

        if (category == null) {
            category = CookingRecipeCategory.MISC;
        }

        if (group == null) {
            group = outputPath;
        }

        SmeltingRecipe recipe = new SmeltingRecipe(group, category, input, output, experience, cookingTime);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addSmokingSmeltingRecipe(Ingredient input, ItemStack output,
                                         float experience, int cookingTime,
                                         @Nullable CookingRecipeCategory category, @Nullable String group,
                                         final ResourceCondition... conditions) {
        String outputPath = getItemPath(output.getItem());

        Identifier recipeId = provider.computeRecipeIdentifier("cooking/smoking",
                outputPath,
                conditions
        );

        if (category == null) {
            category = CookingRecipeCategory.MISC;
        }

        if (group == null) {
            group = outputPath;
        }

        BlastingRecipe recipe = new BlastingRecipe(group, category, input, output, experience, cookingTime);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addBlastingSmeltingRecipe(Ingredient input, ItemStack output,
                                          float experience, int cookingTime,
                                          @Nullable CookingRecipeCategory category, @Nullable String group,
                                          final ResourceCondition... conditions) {
        String outputPath = getItemPath(output.getItem());

        Identifier recipeId = provider.computeRecipeIdentifier("cooking/blasting",
                outputPath,
                conditions
        );

        if (category == null) {
            category = CookingRecipeCategory.MISC;
        }

        if (group == null) {
            group = outputPath;
        }

        BlastingRecipe recipe = new BlastingRecipe(group, category, input, output, experience, cookingTime);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addShapelessCraftingRecipe(DefaultedList<Ingredient> input, ItemStack output,
                                           @Nullable CraftingRecipeCategory category, @Nullable String group,
                                           final ResourceCondition... conditions) {
        String outputPath = getItemPath(output.getItem());

        Identifier recipeId = provider.computeRecipeIdentifier("crafting/shapeless",
                outputPath,
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = outputPath;
        }

        ShapelessRecipe recipe = new ShapelessRecipe(group, category, output, input);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addMakeshiftShapelessCraftingRecipe(DefaultedList<Ingredient> input, ItemStack output,
                                                    List<Ingredient> constantIngredients,
                                                    @Nullable CraftingRecipeCategory category, @Nullable String group,
                                                    final ResourceCondition... conditions) {
        String outputPath = getItemPath(output.getItem());

        Identifier recipeId = provider.computeRecipeIdentifier("crafting/makeshift_shapeless",
                outputPath,
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = outputPath;
        }

        MakeshiftShapelessCraftingRecipe recipe = new MakeshiftShapelessCraftingRecipe(group, category, output, input, constantIngredients);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }


    public void addShapedCraftingRecipe(Map<Character, Ingredient> key, String[] pattern, ItemStack output,
                                        @Nullable CraftingRecipeCategory category, @Nullable String group,
                                        final ResourceCondition... conditions) {
        String outputPath = getItemPath(output.getItem());

        Identifier recipeId = provider.computeRecipeIdentifier("crafting/shaped",
                outputPath,
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = outputPath;
        }

        ShapedRecipe recipe = new ShapedRecipe(group, category, RawShapedRecipe.create(key, Arrays.stream(pattern).toList()), output);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addMakeshiftShapedCraftingRecipe(Map<Character, Ingredient> key, String[] pattern, List<Ingredient> constantIngredients, ItemStack output,
                                                 @Nullable CraftingRecipeCategory category, @Nullable String group,
                                                 final ResourceCondition... conditions) {
        String outputPath = getItemPath(output.getItem());

        Identifier recipeId = provider.computeRecipeIdentifier("crafting/makeshift_shaped",
                outputPath,
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = outputPath;
        }

        ShapedRecipe recipe = new MakeshiftShapedCraftingRecipe(group, category, RawShapedRecipe.create(key, Arrays.stream(pattern).toList()), constantIngredients,  output, false);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addItemExplosionPowerRecipe(NamedIngredient input,
                                            double explosionPower, boolean producesFire, boolean isHidden,  final ResourceCondition... conditions) {

        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.ITEM_EXPLOSION_POWER_RECIPE_ID,
                input.getName(),
                conditions);

        ItemExplosionPowerRecipe recipe = new ItemExplosionPowerRecipe(input.toIngredient(), explosionPower, producesFire, isHidden);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addHammeringRecipe(Ingredient input, ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.HAMMERING_RECIPE_ID,
                getItemPath(output.getItem()),
                conditions);

        HammeringRecipe recipe = new HammeringRecipe(input, output);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addBlastProcessingRecipe(Ingredient input,
                                         double explosionPowerMin, double explosionPowerMax,
                                         ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID,
                getItemPath(output.getItem()),
                conditions);

        BlastProcessingRecipe recipe = new BlastProcessingRecipe(input, explosionPowerMin, explosionPowerMax, output);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addBlastProcessorBehaviorRecipe(NamedIngredient ingredient,
                                                Identifier behaviorIdentifier, final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.BLAST_PROCESSOR_BEHAVIOR_RECIPE_ID,
                ingredient.getName(),
                conditions);

        BlastProcessorBehaviorRecipe recipe = new BlastProcessorBehaviorRecipe(ingredient.toIngredient(), behaviorIdentifier);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addOverrideRecipe(Identifier id) {
        provider.acceptOverrideRecipe(exporter, id);
    }
}