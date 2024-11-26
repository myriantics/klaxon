package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;

import static net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity.PROCESS_ITEM_INDEX;

public class BlastProcessingRecipe implements Recipe<RecipeInput> {
    private final Ingredient processingItem;
    private final double explosionPowerMin;
    private final double explosionPowerMax;
    private final ItemStack result;

    public BlastProcessingRecipe(Ingredient inputA, double explosionPowerMin, double explosionPowerMax, ItemStack result) {
        this.processingItem = inputA;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.result = result;
    }

    @Override
    public boolean matches(RecipeInput inventory, World world) {
        return processingItem.test(inventory.getStackInSlot(PROCESS_ITEM_INDEX));
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    public Ingredient getProcessingItem() {
        return processingItem;
    }

    public double getExplosionPowerMin() {
        return explosionPowerMin;
    }

    public double getExplosionPowerMax() {
        return explosionPowerMax;
    }


    public boolean isCompatibleWithCatalyst(double explosionPower) {
        return explosionPowerMin <= explosionPower && explosionPowerMax >= explosionPower;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.BLAST_PROCESSING;
    }
}
