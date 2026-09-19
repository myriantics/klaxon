package net.myriantics.klaxon.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public interface CompoundOutputRecipe<T extends RecipeInput> extends Recipe<T> {
    ItemStack[] properlyAssemble(T input, HolderLookup.Provider registries);

    ItemStack[] getDisplayStacks(T input, HolderLookup.Provider registries);

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    // not used because it only allows for the output of 1 itemstack (cringe)
    @Deprecated
    @Override
    default ItemStack assemble(T input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    default ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }
}
