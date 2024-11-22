package net.myriantics.klaxon.recipes.blast_processing;

import net.minecraft.item.ItemStack;

public record BlastProcessingRecipeData(double explosionPowerMin, double explosionPowerMax, ItemStack result, BlastProcessingOutputState outputState) {
}
