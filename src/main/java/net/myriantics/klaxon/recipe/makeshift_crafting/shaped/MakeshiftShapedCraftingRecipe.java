package net.myriantics.klaxon.recipe.makeshift_crafting.shaped;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.List;

public class MakeshiftShapedCraftingRecipe extends ShapedRecipe {

    public ItemStack result;

    public RawShapedRecipe raw;

    List<Ingredient> constantIngredients;

    public MakeshiftShapedCraftingRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, List<Ingredient> constantIngredients, ItemStack result, boolean showNotification) {
        super(group, category, raw, result, showNotification);
        this.raw = raw;
        this.result = result;
        this.constantIngredients = constantIngredients;
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

        // decrease the result's durability according to how many makeshift stacks were used in crafting out of all potential makeshift ingredients in the recipe
        result.setDamage((int) (result.getMaxDamage() * durabilityPenaltyCap * ((double) totalPresentMakeshiftIngredients / totalPotentialMakeshiftIngredients)));

        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.MAKESHIFT_SHAPED_CRAFTING_RECIPE_SERIALIZER;
    }

    public ItemStack getRawResult() {
        return result.copy();
    }

    public List<Ingredient> getConstantIngredients() {
        return constantIngredients;
    }
}
