package net.myriantics.klaxon.datagen.recipe;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.NamedIngredient;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public class KlaxonOreProcessingRecipeProvider extends KlaxonRecipeSubProvider{

    public KlaxonOreProcessingRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildFracturedOreProcessingRecipes();
    }

    private void buildFracturedOreProcessingRecipes() {
        addFracturedOreProcessingRecipes(KlaxonItems.FRACTURED_RAW_IRON, Items.RAW_IRON, KlaxonItems.FRACTURED_IRON, Items.IRON_INGOT);
        addFracturedOreProcessingRecipes(KlaxonItems.FRACTURED_RAW_GOLD, Items.RAW_GOLD, KlaxonItems.FRACTURED_GOLD, Items.GOLD_INGOT);
        addFracturedOreProcessingRecipes(KlaxonItems.FRACTURED_RAW_COPPER, Items.RAW_COPPER, KlaxonItems.FRACTURED_COPPER, Items.COPPER_INGOT);
    }

    public void addFracturedOreProcessingRecipes(Item fracturedRawOreItem, Item rawOreItem, Item fracturedOreFragmentsItem, Item oreIngotItem,
                                                 final ResourceCondition... conditions) {
        // blast processing
        addBlastProcessingRecipe(NamedIngredient.ofItems(rawOreItem), 0.4, 1.4,
                builder -> builder
                        .guaranteed(new ItemStack(fracturedRawOreItem))
                        .chance(new ItemStack(fracturedRawOreItem), 1d/3),
                conditions
        );
        addBlastProcessingRecipe(NamedIngredient.ofItems(oreIngotItem), 0.5, 1.7,
                builder -> builder
                        .guaranteed(new ItemStack(fracturedOreFragmentsItem))
                        .chance(new ItemStack(fracturedOreFragmentsItem), 1d/3),
                conditions
        );

        // smelting
        addBlastingAndSmeltingRecipe(Ingredient.ofItems(fracturedRawOreItem), new ItemStack(fracturedOreFragmentsItem), 1.0f, 150, null, "fractured_ores", conditions);

        // crafting
        add2x2PackingRecipe(Ingredient.ofItems(fracturedRawOreItem), new ItemStack(rawOreItem), null, null, conditions);
        add2x2PackingRecipe(Ingredient.ofItems(fracturedOreFragmentsItem), new ItemStack(oreIngotItem), null, null, conditions);
    }
}
