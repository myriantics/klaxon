package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.item.ItemStack;

import java.util.List;

public record BlastProcessingRecipeData(double explosionPowerMin, double explosionPowerMax, ItemStack[] outputStacks) {
}
