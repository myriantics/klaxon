package net.myriantics.klaxon.recipe.world_item_application;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public record WorldItemApplicationRecipeInput(ItemStack usedStack, BlockState inputState) implements RecipeInput {

    @Override
    public ItemStack getItem(int slot) {
        return usedStack();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return RecipeInput.super.isEmpty() && inputState != null;
    }
}
