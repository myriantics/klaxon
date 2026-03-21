package net.myriantics.klaxon.recipe.makeshift_crafting.shaped;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.List;

public class MakeshiftShapedCraftingRecipe extends ShapedRecipe {

    public ItemStack result;

    public ShapedRecipePattern raw;

    List<Ingredient> constantIngredients;

    public MakeshiftShapedCraftingRecipe(String group, CraftingBookCategory category, ShapedRecipePattern raw, List<Ingredient> constantIngredients, ItemStack result, boolean showNotification) {
        super(group, category, raw, result, showNotification);
        this.raw = raw;
        this.result = result;
        this.constantIngredients = constantIngredients;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingRecipeInput, HolderLookup.Provider wrapperLookup) {
        List<ItemStack> inputStacks = craftingRecipeInput.items();

        ItemStack result = this.getResultItem(wrapperLookup);

        final double durabilityPenaltyCap = 0.5;
        int totalPresentMakeshiftIngredients = 0;
        int totalPotentialMakeshiftIngredients = 0;

        for (ItemStack inputStack : inputStacks) {

            // checks to see if the marked ingredient has been indicated to have a makeshift replacement
            if (!this.constantIngredients.stream().anyMatch((ingredient -> ingredient.test(inputStack))) && !inputStack.isEmpty()) {
                totalPotentialMakeshiftIngredients++;

                totalPresentMakeshiftIngredients += inputStack.is(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS) ? 1 : 0;
            }
        }

        // decrease the result's durability according to how many makeshift stacks were used in crafting out of all potential makeshift ingredients in the recipe
        result.setDamageValue((int) (result.getMaxDamage() * durabilityPenaltyCap * ((double) totalPresentMakeshiftIngredients / totalPotentialMakeshiftIngredients)));

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
