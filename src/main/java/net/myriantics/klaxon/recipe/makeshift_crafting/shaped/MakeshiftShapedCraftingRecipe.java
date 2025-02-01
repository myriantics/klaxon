package net.myriantics.klaxon.recipe.makeshift_crafting.shaped;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.List;

public class MakeshiftShapedCraftingRecipe extends ShapedRecipe {

    public ItemStack result;

    public RawShapedRecipe raw;

    List<Ingredient> potentialMakeshiftIngredients;

    public MakeshiftShapedCraftingRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, List<Ingredient> potentialMakeshiftIngredients, ItemStack result, boolean showNotification) {
        super(group, category, raw, result, showNotification);
        this.raw = raw;
        this.result = result;
        this.potentialMakeshiftIngredients = potentialMakeshiftIngredients;
    }

    @Override
    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        return super.matches(craftingRecipeInput, world);
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
            if (this.potentialMakeshiftIngredients.stream().anyMatch((ingredient -> ingredient.test(inputStack)))) {
                totalPotentialMakeshiftIngredients++;

                totalPresentMakeshiftIngredients += inputStack.isIn(KlaxonTags.Items.MAKESHIFT_CRAFTING_INGREDIENTS) ? 1 : 0;
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

    public List<Ingredient> getPotentialMakeshiftIngredients() {
        return potentialMakeshiftIngredients;
    }
}
