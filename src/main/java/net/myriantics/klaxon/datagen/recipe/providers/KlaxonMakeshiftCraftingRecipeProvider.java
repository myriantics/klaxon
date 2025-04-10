package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.List;
import java.util.Map;

public class KlaxonMakeshiftCraftingRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonMakeshiftCraftingRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(RecipeExporter exporter) {
        buildEquipmentCraftingRecipes(exporter);
    }

    private void buildEquipmentCraftingRecipes(RecipeExporter exporter) {
        addMakeshiftShapedCraftingRecipe(exporter, Map.of(
                        'B', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_BLOCKS),
                        'I', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS),
                        'S', Ingredient.ofItems(Items.STICK),
                        'L', Ingredient.ofItems(Items.LEATHER)),
                new String[]{
                        "BIB",
                        " S ",
                        "LSL"
                },
                List.of(Ingredient.ofItems(Items.STICK), Ingredient.ofItems(Items.LEATHER)),
                new ItemStack(KlaxonItems.STEEL_HAMMER),
                CraftingRecipeCategory.EQUIPMENT,
                null
        );

        addMakeshiftShapedCraftingRecipe(exporter, Map.of(
                        'P', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'I', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS)),
                new String[]{
                        "PIP",
                        "P P"
                },
                List.of(),
                new ItemStack(KlaxonItems.STEEL_HELMET),
                CraftingRecipeCategory.EQUIPMENT,
                null
        );

        addMakeshiftShapedCraftingRecipe(exporter, Map.of(
                        'P', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'I', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS),
                        'B', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_BLOCKS)
                ),
                new String[]{
                        "I I",
                        "PBP",
                        "PPP"
                },
                List.of(),
                new ItemStack(KlaxonItems.STEEL_CHESTPLATE),
                CraftingRecipeCategory.EQUIPMENT,
                null
        );

        addMakeshiftShapedCraftingRecipe(exporter, Map.of(
                        'P', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'I', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS)
                ),
                new String[]{
                        "IPI",
                        "P P",
                        "P P"
                },
                List.of(),
                new ItemStack(KlaxonItems.STEEL_LEGGINGS),
                CraftingRecipeCategory.EQUIPMENT,
                null
        );

        addMakeshiftShapedCraftingRecipe(exporter, Map.of(
                        'P', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'I', Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS)
                ),
                new String[]{
                        "P P",
                        "I I"
                },
                List.of(),
                new ItemStack(KlaxonItems.STEEL_BOOTS),
                CraftingRecipeCategory.EQUIPMENT,
                null
        );

        addMakeshiftShapelessCraftingRecipe(exporter,
                DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS),
                        Ingredient.ofItems(Items.FLINT)),
                new ItemStack(Items.FLINT_AND_STEEL),
                List.of(Ingredient.ofItems(Items.FLINT)),
                null, null);
    }
}
