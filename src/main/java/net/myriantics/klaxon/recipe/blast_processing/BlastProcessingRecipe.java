package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;

import static net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX;

public class BlastProcessingRecipe implements Recipe<RecipeInput> {
    private final Ingredient ingredientItem;
    private final double explosionPowerMin;
    private final double explosionPowerMax;
    private final ItemStack result;

    public BlastProcessingRecipe(Ingredient inputA, double explosionPowerMin, double explosionPowerMax, ItemStack result) {
        this.ingredientItem = inputA;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.result = result;
    }

    @Override
    public boolean matches(RecipeInput inventory, World world) {
        return ingredientItem.test(inventory.getStackInSlot(INGREDIENT_INDEX));
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

    public Ingredient getIngredientItem() {
        return ingredientItem;
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
