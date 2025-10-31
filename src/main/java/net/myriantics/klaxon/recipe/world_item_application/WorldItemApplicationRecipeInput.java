package net.myriantics.klaxon.recipe.world_item_application;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record WorldItemApplicationRecipeInput(ItemStack usedStack, BlockState inputState) implements RecipeInput {

    @Override
    public ItemStack getStackInSlot(int slot) {
        return usedStack();
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return RecipeInput.super.isEmpty() && inputState != null;
    }
}
