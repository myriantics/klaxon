package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

import static net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX;

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
    public boolean matches(BlastProcessingRecipeInput inventory, Level world) {
        return ingredientItem.test(inventory.getItem(INGREDIENT_INDEX));
    }

    @Override
    public ItemStack assemble(BlastProcessingRecipeInput input, HolderLookup.Provider lookup) {
        return ItemStack.EMPTY;
    }

    public ItemStack[] craft(BlastProcessingRecipeInput input, HolderLookup.Provider lookup, RandomSource random) {
        double explosionPower = input.getPowerData().explosionPower();

        // check if explosion power exists and is within bounds
        if (explosionPower > 0 && explosionPower >= explosionPowerMin && explosionPower <= explosionPowerMax) {
            return getDrops(input, lookup, random);
        }

        return new ItemStack[0];
    }

    protected ItemStack[] getDrops(BlastProcessingRecipeInput input, HolderLookup.Provider provider, RandomSource randomSource) {
        return recipeOutputCompound.computeDrops(randomSource);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
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


    public boolean isCompatibleWithCatalyst(ExplosiveCatalystData data) {
        return data.matchesConditions(this.explosionPowerMin, this.explosionPowerMax);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR);
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
