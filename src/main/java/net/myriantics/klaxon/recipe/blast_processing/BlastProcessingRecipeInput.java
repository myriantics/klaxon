package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public class BlastProcessingRecipeInput implements RecipeInput {
    private final ItemExplosionPowerData powerData;
    private final ItemStack inputStack;

    public BlastProcessingRecipeInput(ItemStack inputStack, ItemExplosionPowerData powerData) {
        this.inputStack = inputStack;
        this.powerData = powerData;
    }

    public ItemExplosionPowerData getPowerData() {
        return powerData;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? inputStack : ItemStack.EMPTY;
    }

    @Override
    public int getSize() {
        return 1;
    }
}
