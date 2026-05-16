package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;

import java.util.Random;

public class BlastProcessingRecipeInput implements RecipeInput {
    private final ExplosiveCatalystData catalystData;
    private final ItemStack ingredientStack;
    private final RandomSource random;

    public BlastProcessingRecipeInput(ItemStack ingredientStack, ExplosiveCatalystData catalystData, RandomSource random) {
        this.ingredientStack = ingredientStack;
        this.catalystData = catalystData;
        this.random = random;
    }

    public ItemStack getIngredientStack() {
        return this.ingredientStack;
    }

    public ExplosiveCatalystData getCatalystData() {
        return this.catalystData == null ? ExplosiveCatalystData.ZERO : this.catalystData;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? ingredientStack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    public RandomSource getRandom() {
        return this.random;
    }
}
