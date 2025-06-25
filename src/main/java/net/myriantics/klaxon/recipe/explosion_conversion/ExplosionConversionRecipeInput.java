package net.myriantics.klaxon.recipe.explosion_conversion;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record ExplosionConversionRecipeInput(BlockState catalystState, BlockState inputBlockState) implements RecipeInput {

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
        return catalystState == null && inputBlockState == null;
    }
}
