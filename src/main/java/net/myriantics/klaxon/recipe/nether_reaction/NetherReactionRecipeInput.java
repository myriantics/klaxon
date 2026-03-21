package net.myriantics.klaxon.recipe.nether_reaction;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public record NetherReactionRecipeInput(BlockState inputBlockState) implements RecipeInput {

    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return inputBlockState == null;
    }
}
