package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.KlaxonBlocks;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;

import static net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX;

public class BlastProcessingRecipe implements Recipe<Inventory> {
    private final Ingredient ingredientItem;
    private final double explosionPowerMin;
    private final double explosionPowerMax;
    private final ItemStack result;

    private final Identifier identifier;

    public BlastProcessingRecipe(Identifier id, Ingredient inputA, double explosionPowerMin, double explosionPowerMax, ItemStack result) {
        this.ingredientItem = inputA;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.result = result;
        this.identifier = id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return ingredientItem.test(inventory.getStack(INGREDIENT_INDEX));
    }

    @Override
    public ItemStack craft(Inventory input, DynamicRegistryManager registryManager) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.result;
    }

    public Ingredient getIngredient() {
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

    @Override
    public Identifier getId() {
        return identifier;
    }
}
