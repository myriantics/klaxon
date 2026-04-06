package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.List;
import java.util.Map;

public class KlaxonMakeshiftCraftingRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonMakeshiftCraftingRecipeProvider(KlaxonRecipeProvider provider, RecipeOutput exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildShapedCraftingRecipes();
        buildShapelessCraftingRecipes();
    }

    private void buildShapedCraftingRecipes() {

        addMakeshiftShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'I', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS)),
                new String[]{
                        "PIP",
                        "P P"
                },
                List.of(),
                new ItemStack(KlaxonItems.STEEL_HELMET),
                CraftingBookCategory.EQUIPMENT,
                null
        );

        addMakeshiftShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'I', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS),
                        'C', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_CASING)
                ),
                new String[]{
                        "I I",
                        "PCP",
                        "PPP"
                },
                List.of(),
                new ItemStack(KlaxonItems.STEEL_CHESTPLATE),
                CraftingBookCategory.EQUIPMENT,
                null
        );

        addMakeshiftShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'I', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS)
                ),
                new String[]{
                        "IPI",
                        "P P",
                        "P P"
                },
                List.of(),
                new ItemStack(KlaxonItems.STEEL_LEGGINGS),
                CraftingBookCategory.EQUIPMENT,
                null
        );

        addMakeshiftShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'I', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS)
                ),
                new String[]{
                        "P P",
                        "I I"
                },
                List.of(),
                new ItemStack(KlaxonItems.STEEL_BOOTS),
                CraftingBookCategory.EQUIPMENT,
                null
        );

        addMakeshiftShapedCraftingRecipe(Map.of(
                        'B', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_BLOCKS),
                        'I', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS),
                        'G', Ingredient.of(KlaxonItemTags.GEAR_GRIP_MATERIALS),
                        'S', Ingredient.of(Items.STICK)),
                new String[]{
                        "BIB",
                        " S ",
                        "GSG"
                },
                List.of(Ingredient.of(Items.STICK), Ingredient.of(KlaxonItemTags.GEAR_GRIP_MATERIALS)),
                new ItemStack(KlaxonItems.STEEL_HAMMER),
                CraftingBookCategory.EQUIPMENT,
                null
        );
        addMakeshiftShapedCraftingRecipe(Map.of(
                'P', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                'G', Ingredient.of(KlaxonItemTags.GEAR_GRIP_MATERIALS),
                'S', Ingredient.of(Items.STICK)
                ),
                new String[]{
                        "PP",
                        "PP",
                        "SG"
                },
                List.of(Ingredient.of(Items.STICK), Ingredient.of(KlaxonItemTags.GEAR_GRIP_MATERIALS)),
                new ItemStack(KlaxonItems.STEEL_CLEAVER),
                CraftingBookCategory.EQUIPMENT,
                null
        );
        addMakeshiftShapedCraftingRecipe(Map.of(
                'P', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                'G', Ingredient.of(KlaxonItemTags.GEAR_GRIP_MATERIALS)
                ),
                new String[] {
                        "GP",
                        "PG"
                },
                List.of(Ingredient.of(KlaxonItemTags.GEAR_GRIP_MATERIALS)),
                new ItemStack(KlaxonItems.STEEL_CABLE_SHEARS),
                CraftingBookCategory.EQUIPMENT,
                null
        );
        addMakeshiftShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'I', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS),
                        'G', Ingredient.of(KlaxonItemTags.GEAR_GRIP_MATERIALS),
                        'S', Ingredient.of(Items.STICK)
                ),
                new String[] {
                        " PP",
                        " II",
                        "SG "
                },
                List.of(Ingredient.of(Items.STICK), Ingredient.of(KlaxonItemTags.GEAR_GRIP_MATERIALS)),
                new ItemStack(KlaxonItems.STEEL_WRENCH),
                CraftingBookCategory.EQUIPMENT,
                null
        );
        addMakeshiftShapedCraftingRecipe(Map.of(
                        'P', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES),
                        'N', Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS),
                        'F', Ingredient.of(Items.FIRE_CHARGE)
                ),
                new String[] {
                        "PN",
                        "FP",
                },
                List.of(Ingredient.of(Items.FIRE_CHARGE)),
                new ItemStack(KlaxonItems.STEEL_LIGHTER),
                CraftingBookCategory.EQUIPMENT,
                null
        );
    }

    private void buildShapelessCraftingRecipes() {
    }
}
