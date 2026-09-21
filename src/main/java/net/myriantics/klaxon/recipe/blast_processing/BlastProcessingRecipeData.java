package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record BlastProcessingRecipeData(List<ItemStack> outputStacks, double explosionPowerMin, double explosionPowerMax,
                                        int ingredientsProcessed, boolean success) {
    public BlastProcessingRecipeData(List<ItemStack> outputStacks, double explosionPowerMin, double explosionPowerMax, int ingredientsProcessed) {
        this(outputStacks, explosionPowerMin, explosionPowerMax, ingredientsProcessed, true);
    }


    public static final BlastProcessingRecipeData ZERO = new BlastProcessingRecipeData(List.of(), 0.0, 0.0, 0, false);
}
