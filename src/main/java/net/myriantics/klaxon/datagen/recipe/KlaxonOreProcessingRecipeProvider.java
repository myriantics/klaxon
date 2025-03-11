package net.myriantics.klaxon.datagen.recipe;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.registry.KlaxonItems;

public class KlaxonOreProcessingRecipeProvider extends KlaxonRecipeSubProvider{

    public KlaxonOreProcessingRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(RecipeExporter exporter) {
        buildFracturedOreProcessingRecipes(exporter);
    }

    private void buildFracturedOreProcessingRecipes(RecipeExporter exporter) {
        addFracturedOreProcessingRecipes(exporter, KlaxonItems.FRACTURED_RAW_IRON, Items.RAW_IRON, KlaxonItems.FRACTURED_IRON_FRAGMENTS, Items.IRON_INGOT);
        addFracturedOreProcessingRecipes(exporter, KlaxonItems.FRACTURED_RAW_GOLD, Items.RAW_GOLD, KlaxonItems.FRACTURED_GOLD_FRAGMENTS, Items.GOLD_INGOT);
        addFracturedOreProcessingRecipes(exporter, KlaxonItems.FRACTURED_RAW_COPPER, Items.RAW_COPPER, KlaxonItems.FRACTURED_COPPER_FRAGMENTS, Items.COPPER_INGOT);
    }

    public void addFracturedOreProcessingRecipes(RecipeExporter exporter,
                                                 Item fracturedRawOreItem, Item rawOreItem, Item fracturedOreFragmentsItem, Item oreIngotItem,
                                                 final ResourceCondition... conditions) {
        // blast processing
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(rawOreItem), 0.1, 0.4, new ItemStack(fracturedRawOreItem), conditions);
        addBlastProcessingRecipe(exporter, Ingredient.ofItems(oreIngotItem), 0.2, 0.5, new ItemStack(fracturedOreFragmentsItem), conditions);

        // smelting
        addOreProcessingCookingRecipe(exporter, Ingredient.ofItems(fracturedRawOreItem), new ItemStack(fracturedOreFragmentsItem), 1.0f, 150, null, "fractured_ores", conditions);

        // crafting
        add2x2PackingRecipe(exporter, Ingredient.ofItems(fracturedRawOreItem), new ItemStack(rawOreItem), null, null, conditions);
        add2x2PackingRecipe(exporter, Ingredient.ofItems(fracturedOreFragmentsItem), new ItemStack(oreIngotItem), null, null, conditions);
    }
}
