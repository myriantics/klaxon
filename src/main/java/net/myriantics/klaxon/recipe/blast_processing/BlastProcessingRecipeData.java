package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.world.item.ItemStack;

public record BlastProcessingRecipeData(double explosionPowerMin, double explosionPowerMax, ItemStack[] outputStacks) {
    public static final BlastProcessingRecipeData ZERO = new BlastProcessingRecipeData(0.0, 0.0, new ItemStack[]{});
}
