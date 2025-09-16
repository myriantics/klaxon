package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.RecipeOutputCompound;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

import java.util.List;

import static net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX;

public class BlastProcessingRecipe implements Recipe<BlastProcessingRecipeInput> {
    private final Ingredient ingredientItem;
    private final double explosionPowerMin;
    private final double explosionPowerMax;
    private final RecipeOutputCompound recipeOutputCompound;

    public BlastProcessingRecipe(Ingredient inputA, double explosionPowerMin, double explosionPowerMax, RecipeOutputCompound result) {
        this.ingredientItem = inputA;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.recipeOutputCompound = result;
    }

    @Override
    public boolean matches(BlastProcessingRecipeInput inventory, World world) {
        return ingredientItem.test(inventory.getStackInSlot(INGREDIENT_INDEX));
    }

    @Override
    public ItemStack craft(BlastProcessingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return ItemStack.EMPTY;
    }

    public ItemStack[] craft(BlastProcessingRecipeInput input, RegistryWrapper.WrapperLookup lookup, Random random) {
        double explosionPower = input.getPowerData().explosionPower();

        // check if explosion power exists and is within bounds
        if (explosionPower > 0 && explosionPower >= explosionPowerMin && explosionPower <= explosionPowerMax) {
            return recipeOutputCompound.computeDrops(random);
        }

        return new ItemStack[0];
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
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

    public RecipeOutputCompound getRecipeOutputCompound() {
        return recipeOutputCompound;
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
