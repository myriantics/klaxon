package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.item.ItemStack;

public record BlastProcessingRecipeData(double explosionPowerMin, double explosionPowerMax, ItemStack result) {
}
