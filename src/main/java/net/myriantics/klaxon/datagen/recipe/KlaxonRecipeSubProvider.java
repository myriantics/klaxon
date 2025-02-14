package net.myriantics.klaxon.datagen.recipe;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;
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

    public KlaxonRecipeSubProvider(KlaxonRecipeProvider provider) {
        this.provider = provider;
    }

    public void generateRecipes(RecipeExporter exporter) {
        KlaxonCommon.LOGGER.error("Override this lol");
    }

    // recipe adding code below (to be used by subclasses)

    public void add3x3UnpackingRecipe(RecipeExporter exporter,
                                      Ingredient input, ItemConvertible output,
                                      @Nullable CraftingRecipeCategory category, @Nullable String group,
                                      final ResourceCondition... conditions) {
        addShapelessCraftingRecipe(exporter, DefaultedList.copyOf(Ingredient.EMPTY, input), new ItemStack(output, 9), category, group, conditions);
    }

    public void add3x3PackingRecipe(RecipeExporter exporter,
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

    public void add2x2UnpackingRecipe(RecipeExporter exporter,
                                      Ingredient input, ItemConvertible output,
                                      @Nullable CraftingRecipeCategory category, @Nullable String group,
                                      final ResourceCondition... conditions) {
        addShapelessCraftingRecipe(exporter, DefaultedList.copyOf(Ingredient.EMPTY, input), new ItemStack(output, 4), category, group, conditions);
    }

    public void add2x2PackingRecipe(RecipeExporter exporter,
                                    Ingredient input, ItemStack output,
                                    @Nullable CraftingRecipeCategory category, @Nullable String group,
                                    final ResourceCondition... conditions) {
        String[] pattern = {
                "xx",
                "xx"
        };

        addShapedCraftingRecipe(exporter, Map.of('x', input), pattern, output, category, group, conditions);
    }

    public void addFoodProcessingCookingRecipe(RecipeExporter exporter,
                                               Ingredient input, ItemStack output,
                                               float experience, int cookingTime,
                                               @Nullable String group,
                                               final ResourceCondition... conditions) {
        addSmokingSmeltingRecipe(exporter,
                input, output, experience, (int) (cookingTime * 0.5),
                CookingRecipeCategory.FOOD, group, conditions);
        addSmeltingRecipe(exporter, input, output, experience, cookingTime, CookingRecipeCategory.FOOD, group, conditions);
    }

    public void addOreProcessingCookingRecipe(RecipeExporter exporter,
                                              Ingredient input, ItemStack output,
                                              float experience, int cookingTime,
                                              @Nullable CookingRecipeCategory category, @Nullable String group,
                                              final ResourceCondition... conditions) {
        addBlastingSmeltingRecipe(exporter,
                input, output, experience, (int) (cookingTime * 0.5),
                category, group, conditions);
        addSmeltingRecipe(exporter, input, output, experience, cookingTime, category, group, conditions);
    }

    public void addSmeltingRecipe(RecipeExporter exporter,
                                  Ingredient input, ItemStack output,
                                  float experience, int cookingTime,
                                  @Nullable CookingRecipeCategory category, @Nullable String group,
                                  final ResourceCondition... conditions) {

        Identifier recipeId = provider.computeRecipeIdentifier("cooking/smelting",
                getItemPath(output.getItem()),
                conditions
        );

        if (category == null) {
            category = CookingRecipeCategory.MISC;
        }

        if (group == null) {
            group = getItemPath(output.getItem());
        }

        SmeltingRecipe recipe = new SmeltingRecipe(group, category, input, output, experience, cookingTime);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addSmokingSmeltingRecipe(RecipeExporter exporter,
                                         Ingredient input, ItemStack output,
                                         float experience, int cookingTime,
                                         @Nullable CookingRecipeCategory category, @Nullable String group,
                                         final ResourceCondition... conditions) {

        Identifier recipeId = provider.computeRecipeIdentifier("cooking/smoking",
                getItemPath(output.getItem()),
                conditions
        );

        if (category == null) {
            category = CookingRecipeCategory.MISC;
        }

        if (group == null) {
            group = getItemPath(output.getItem());
        }

        BlastingRecipe recipe = new BlastingRecipe(group, category, input, output, experience, cookingTime);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addBlastingSmeltingRecipe(RecipeExporter exporter,
                                          Ingredient input, ItemStack output,
                                          float experience, int cookingTime,
                                          @Nullable CookingRecipeCategory category, @Nullable String group,
                                          final ResourceCondition... conditions) {

        Identifier recipeId = provider.computeRecipeIdentifier("cooking/blasting",
                getItemPath(output.getItem()),
                conditions
        );

        if (category == null) {
            category = CookingRecipeCategory.MISC;
        }

        if (group == null) {
            group = getItemPath(output.getItem());
        }

        BlastingRecipe recipe = new BlastingRecipe(group, category, input, output, experience, cookingTime);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addShapelessCraftingRecipe(RecipeExporter exporter,
                                           DefaultedList<Ingredient> input, ItemStack output,
                                           @Nullable CraftingRecipeCategory category, @Nullable String group,
                                           final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier("crafting/shapeless",
                getItemPath(output.getItem()),
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = getItemPath(output.getItem());
        }

        ShapelessRecipe recipe = new ShapelessRecipe(group, category, output, input);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addMakeshiftShapelessCraftingRecipe(RecipeExporter exporter,
                                                    DefaultedList<Ingredient> input, ItemStack output,
                                                    List<Ingredient> constantIngredients,
                                                    @Nullable CraftingRecipeCategory category, @Nullable String group,
                                                    final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier("crafting/makeshift_shapeless",
                getItemPath(output.getItem()),
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = getItemPath(output.getItem());
        }

        MakeshiftShapelessCraftingRecipe recipe = new MakeshiftShapelessCraftingRecipe(group, category, output, input, constantIngredients);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }


    public void addShapedCraftingRecipe(RecipeExporter exporter,
                                        Map<Character, Ingredient> key, String[] pattern, ItemStack output,
                                        @Nullable CraftingRecipeCategory category, @Nullable String group,
                                        final ResourceCondition... conditions) {

        Identifier recipeId = provider.computeRecipeIdentifier("crafting/shaped",
                getItemPath(output.getItem()),
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = getItemPath(output.getItem());
        }

        ShapedRecipe recipe = new ShapedRecipe(group, category, RawShapedRecipe.create(key, Arrays.stream(pattern).toList()), output);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addMakeshiftShapedCraftingRecipe(RecipeExporter exporter,
                                                 Map<Character, Ingredient> key, String[] pattern, List<Ingredient> constantIngredients, ItemStack output,
                                                 @Nullable CraftingRecipeCategory category, @Nullable String group,
                                                 final ResourceCondition... conditions) {

        Identifier recipeId = provider.computeRecipeIdentifier("crafting/makeshift_shaped",
                getItemPath(output.getItem()),
                conditions);

        if (category == null) {
            category = CraftingRecipeCategory.MISC;
        }

        if (group == null) {
            group = getItemPath(output.getItem());
        }

        ShapedRecipe recipe = new MakeshiftShapedCraftingRecipe(group, category, RawShapedRecipe.create(key, Arrays.stream(pattern).toList()), constantIngredients,  output, false);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addItemExplosionPowerRecipe(RecipeExporter exporter, Ingredient input,
                                            double explosionPower, boolean producesFire, boolean isHidden,  final ResourceCondition... conditions) {

        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.ITEM_EXPLOSION_POWER_RECIPE_ID,
                Registries.ITEM.getId(input.getMatchingStacks()[0].getItem()).getPath(),
                conditions);

        ItemExplosionPowerRecipe recipe = new ItemExplosionPowerRecipe(input, explosionPower, producesFire, isHidden);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addHammeringRecipe(RecipeExporter exporter, Ingredient input, ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.HAMMERING_RECIPE_ID,
                getItemPath(output.getItem()),
                conditions);

        HammeringRecipe recipe = new HammeringRecipe(input, output);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addBlastProcessingRecipe(RecipeExporter exporter, Ingredient input,
                                         double explosionPowerMin, double explosionPowerMax,
                                         ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID,
                getItemPath(output.getItem()),
                conditions);

        BlastProcessingRecipe recipe = new BlastProcessingRecipe(input, explosionPowerMin, explosionPowerMax, output);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }
}