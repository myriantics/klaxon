package net.myriantics.klaxon.recipe.nether_reaction;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record NetherReactionRecipeInput(BlockState inputBlockState) implements RecipeInput {

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return inputBlockState == null;
    }
}
