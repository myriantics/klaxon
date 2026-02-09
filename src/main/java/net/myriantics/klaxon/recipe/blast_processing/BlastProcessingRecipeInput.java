package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class BlastProcessingRecipeInput implements RecipeInput {
    private final ExplosiveCatalystData powerData;
    private final ItemStack inputStack;

    public BlastProcessingRecipeInput(ItemStack inputStack, ExplosiveCatalystData powerData) {
        this.inputStack = inputStack;
        this.powerData = powerData;
    }

    public ExplosiveCatalystData getPowerData() {
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
