package net.myriantics.klaxon.recipe.makeshift_crafting.shapeless;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.List;

public class MakeshiftShapelessCraftingRecipe extends ShapelessRecipe {


    private final ItemStack result;
    List<Ingredient> constantIngredients;
    DefaultedList<Ingredient> ingredients;


    public MakeshiftShapelessCraftingRecipe(String group, CraftingRecipeCategory category, ItemStack result, DefaultedList<Ingredient> ingredients, List<Ingredient> constantIngredients) {
        super(group, category, result, ingredients);
        this.constantIngredients = constantIngredients;
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        List<ItemStack> inputStacks = craftingRecipeInput.getStacks();

        ItemStack result = this.getResult(wrapperLookup);

        final double durabilityPenaltyCap = 0.5;
        int totalPresentMakeshiftIngredients = 0;
        int totalPotentialMakeshiftIngredients = 0;

        for (ItemStack inputStack : inputStacks) {

            // checks to see if the marked ingredient has been indicated to have a makeshift replacement
            if (!this.constantIngredients.stream().anyMatch((ingredient -> ingredient.test(inputStack))) && !inputStack.isEmpty()) {
                totalPotentialMakeshiftIngredients++;

                totalPresentMakeshiftIngredients += inputStack.isIn(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS) ? 1 : 0;
            }
        }

        KlaxonCommon.LOGGER.info("Total present makeshift ingredients: " + totalPresentMakeshiftIngredients);
        KlaxonCommon.LOGGER.info("Total potential makeshift ingredients: " + totalPotentialMakeshiftIngredients);

        // decrease the result's durability according to how many makeshift stacks were used in crafting out of all potential makeshift ingredients in the recipe
        result.setDamage((int) (result.getMaxDamage() * durabilityPenaltyCap * ((double) totalPresentMakeshiftIngredients / totalPotentialMakeshiftIngredients)));

        return result.copy();
    }

    public ItemStack getRawResult() {
        return result;
    }

    public List<Ingredient> getConstantIngredients() {
        return constantIngredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.MAKESHIFT_SHAPELESS_CRAFTING_RECIPE_SERIALIZER;
    }
}
