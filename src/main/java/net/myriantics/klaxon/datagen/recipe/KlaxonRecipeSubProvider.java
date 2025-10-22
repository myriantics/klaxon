package net.myriantics.klaxon.datagen.recipe;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.api.BlockIngredient;
import net.myriantics.klaxon.api.NamedIngredient;
import net.myriantics.klaxon.api.RecipeOutputCompound;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.datagen.custom_providers.KlaxonToolUsageRecipeTypeProvider;
import net.myriantics.klaxon.recipe.cooling.ItemCoolingRecipe;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.manual_item_application.WorldItemApplicationRecipe;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipe;
import net.myriantics.klaxon.registry.behavior.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shaped.MakeshiftShapedCraftingRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shapeless.MakeshiftShapelessCraftingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

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
        addShapelessCraftingRecipe(DefaultedList.copyOf(Ingredient.EMPTY, input), new ItemStack(output, 9), category, group, conditions);
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
        addShapelessCraftingRecipe(DefaultedList.copyOf(Ingredient.EMPTY, input), new ItemStack(output, 4), category, group, conditions);
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

    public void addShapelessCraftingRecipe(Ingredient input, ItemStack output,
                                           @Nullable CraftingRecipeCategory category, @Nullable String group,
                                           final ResourceCondition... conditions) {
        addShapelessCraftingRecipe(DefaultedList.copyOf(Ingredient.EMPTY, input), output, category, group, conditions);
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

    public void addExplosiveCatalystDefinitionRecipe(
            NamedIngredient input,
            double explosionPower, boolean producesFire,
            boolean isHidden,
            final ResourceCondition... conditions
    ) {
        this.addExplosiveCatalystDefinitionRecipe(input, KlaxonExplosiveCatalystBehaviors.DEFAULT, explosionPower, producesFire, isHidden, conditions);
    }

    public void addExplosiveCatalystDefinitionRecipe(
            NamedIngredient input,
            ExplosiveCatalystBehavior behavior,
            double explosionPower, boolean producesFire,
            boolean isHidden,
            final ResourceCondition... conditions
    ) {

        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION_ID,
                input.getName(),
                conditions);

        ExplosiveCatalystDefinitionRecipe recipe = new ExplosiveCatalystDefinitionRecipe(input.toIngredient(), new ExplosiveCatalystData(behavior, explosionPower, producesFire), isHidden);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addHammeringRecipe(Ingredient input, ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonToolUsageRecipeTypeProvider.HAMMERING.getValue().getPath(),
                getItemPath(output.getItem()),
                conditions);

        ToolUsageRecipe recipe = new ToolUsageRecipe(KlaxonToolUsageRecipeTypeProvider.HAMMERING, input, output, null);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addWirecuttingRecipe(Ingredient input, ItemStack output, final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonToolUsageRecipeTypeProvider.WIRECUTTING.getValue().getPath(),
                getItemPath(output.getItem()),
                conditions);

        ToolUsageRecipe recipe = new ToolUsageRecipe(KlaxonToolUsageRecipeTypeProvider.WIRECUTTING, input, output, null);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addManualItemApplicationRecipe(TagKey<Block> validBlockInputs, Ingredient ingredient, Block outputBlock, ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.WORLD_ITEM_APPLICATION_RECIPE_ID,
                Registries.BLOCK.getId(outputBlock).getPath(),
                conditions);

        WorldItemApplicationRecipe recipe = new WorldItemApplicationRecipe(validBlockInputs, ingredient, outputBlock);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addNetherReactionRecipe(TagKey<Block> blockTag, Block outputBlock, ResourceCondition... conditions) {
        addNetherReactionRecipe(BlockIngredient.fromTag(blockTag), outputBlock, conditions);
    }

    public void addNetherReactionRecipe(BlockIngredient blockIngredient, Block outputBlock, ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.NETHER_REACTION_RECIPE_ID,
                Registries.BLOCK.getId(outputBlock).getPath(),
                conditions);

        NetherReactionRecipe recipe = new NetherReactionRecipe(blockIngredient, outputBlock);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addItemCoolingRecipe(Ingredient input, ItemConvertible output, final ResourceCondition... conditions) {
        Identifier recipeId = provider.computeRecipeIdentifier(KlaxonRecipeTypes.COOLING_RECIPE_ID,
                getItemPath(output.asItem()),
                conditions);

        ItemCoolingRecipe recipe = new ItemCoolingRecipe(input, new ItemStack(output));

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }

    public void addBlastProcessingRecipe(NamedIngredient input,
                                         double explosionPowerMin, double explosionPowerMax,
                                         ItemStack output, final ResourceCondition... conditions) {
        addBlastProcessingRecipe(input, explosionPowerMin, explosionPowerMax, RecipeOutputCompound.of(output), conditions);
    }

    public void addBlastProcessingRecipe(NamedIngredient input,
                                         double explosionPowerMin, double explosionPowerMax,
                                         Function<RecipeOutputCompound.Builder, RecipeOutputCompound.Builder> function, final ResourceCondition... conditions) {
        addBlastProcessingRecipe(input, explosionPowerMin, explosionPowerMax, function.apply(RecipeOutputCompound.builder()).build(), conditions);
    }

    public void addExplosiveDisassemblyRecipe(NamedIngredient input,
                                         double explosionPowerMin, double explosionPowerMax,
                                         Function<RecipeOutputCompound.Builder, RecipeOutputCompound.Builder> function, final ResourceCondition... conditions) {
        addBlastProcessingRecipe(input.withName("recycling/" + input.getName()), explosionPowerMin, explosionPowerMax, function.apply(RecipeOutputCompound.builder()).build(), conditions);
    }

    public void addBlastProcessingRecipe(NamedIngredient input,
                                         double explosionPowerMin, double explosionPowerMax,
                                         RecipeOutputCompound outputCompound, final ResourceCondition... conditions) {
        String path = outputCompound.size() > 1
                ? input.getName()
                : getItemPath(outputCompound.getDisplayStacks()[0].getItem()) + "_from_" + input.getName();

        Identifier recipeId = provider.computeRecipeIdentifier(
                KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID,
                path,
                conditions
        );

        BlastProcessingRecipe recipe = new BlastProcessingRecipe(input.toIngredient(), explosionPowerMin, explosionPowerMax, outputCompound);

        provider.acceptRecipeWithConditions(exporter, recipeId, recipe, conditions);
    }


    public void addOverrideRecipe(Identifier id) {
        provider.acceptOverrideRecipe(exporter, id);
    }

    private String[] getInvertedPattern(String[] pattern) {
        String[] invertedPattern = pattern.clone();
        for (int i = 0; i < pattern.length; i++) {
            StringBuilder flippedRow = new StringBuilder();
            for (Character character : pattern[i].toCharArray()) {
                flippedRow.insert(0, character);
            }
            invertedPattern[i] = flippedRow.toString();
        }

        return invertedPattern;
    }
}