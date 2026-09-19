package net.myriantics.klaxon.recipe.shredding.industrial;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record IndustrialShreddingRecipeInput(ItemStack inputStack, RandomSource random) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? inputStack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
