package net.myriantics.klaxon.recipe.makeshift_crafting.shaped;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.List;

public class MakeshiftShapedCraftingRecipe extends ShapedRecipe {
    public Identifier ID = KlaxonCommon.locate(KlaxonRecipeTypes.MAKESHIFT_SHAPED_CRAFTING_ID);

    public ItemStack result;

    List<Ingredient> constantIngredients;

    public MakeshiftShapedCraftingRecipe(Identifier id,  String group, CraftingRecipeCategory category, int width, int height, DefaultedList<Ingredient> input, List<Ingredient> constantIngredients, ItemStack result, boolean showNotification) {
        super(id, group, category, width, height, input, result, showNotification);
        this.result = result;
        this.constantIngredients = constantIngredients;
    }

    @Override
    public ItemStack craft(RecipeInputInventory craftingRecipeInput, DynamicRegistryManager registryManager) {
        List<ItemStack> inputStacks = craftingRecipeInput.getInputStacks();

        ItemStack result = this.getOutput(registryManager).copy();

        final double durabilityPenaltyCap = 0.5;
        int totalPresentMakeshiftIngredients = 0;
        int totalPotentialMakeshiftIngredients = 0;

        for (ItemStack inputStack : inputStacks) {

            // checks to see if the marked ingredient has been indicated to have a makeshift replacement
            if (this.constantIngredients.stream().noneMatch((ingredient -> ingredient.test(inputStack))) && !inputStack.isEmpty()) {
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

    @Override
    public Identifier getId() {
        return ID;
    }
}
