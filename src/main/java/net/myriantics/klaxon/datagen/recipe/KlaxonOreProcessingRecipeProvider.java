package net.myriantics.klaxon.datagen.recipe;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.api.NamedIngredient;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;

public class KlaxonOreProcessingRecipeProvider extends KlaxonRecipeSubProvider{

    public KlaxonOreProcessingRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildFracturedOreProcessingRecipes();
    }

    private void buildFracturedOreProcessingRecipes() {
        addFracturedOreProcessingRecipes(KlaxonItems.FRACTURED_RAW_IRON, Items.RAW_IRON, KlaxonItems.FRACTURED_IRON_FRAGMENTS, Items.IRON_INGOT);
        addFracturedOreProcessingRecipes(KlaxonItems.FRACTURED_RAW_GOLD, Items.RAW_GOLD, KlaxonItems.FRACTURED_GOLD_FRAGMENTS, Items.GOLD_INGOT);
        addFracturedOreProcessingRecipes(KlaxonItems.FRACTURED_RAW_COPPER, Items.RAW_COPPER, KlaxonItems.FRACTURED_COPPER_FRAGMENTS, Items.COPPER_INGOT);
    }

    public void addFracturedOreProcessingRecipes(Item fracturedRawOreItem, Item rawOreItem, Item fracturedOreFragmentsItem, Item oreIngotItem,
                                                 final ResourceCondition... conditions) {
        // blast processing
        addBlastProcessingRecipe(Ingredient.ofItems(rawOreItem), 0.1, 0.4, new ItemStack(fracturedRawOreItem), conditions);
        addBlastProcessingRecipe(Ingredient.ofItems(oreIngotItem), 0.2, 0.5, new ItemStack(fracturedOreFragmentsItem), conditions);

        // smelting
        addOreProcessingCookingRecipe(Ingredient.ofItems(fracturedRawOreItem), new ItemStack(fracturedOreFragmentsItem), 1.0f, 150, null, "fractured_ores", conditions);

        // crafting
        add2x2PackingRecipe(Ingredient.ofItems(fracturedRawOreItem), new ItemStack(rawOreItem), null, null, conditions);
        add2x2PackingRecipe(Ingredient.ofItems(fracturedOreFragmentsItem), new ItemStack(oreIngotItem), null, null, conditions);
    }
}
