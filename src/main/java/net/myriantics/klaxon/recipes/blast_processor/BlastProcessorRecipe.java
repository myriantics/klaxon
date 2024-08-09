package net.myriantics.klaxon.recipes.blast_processor;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity;
import net.myriantics.klaxon.recipes.hammer.HammerRecipeSerializer;

public class BlastProcessorRecipe implements Recipe<SimpleInventory> {
    private final Ingredient processingItem;
    private final double explosionPowerMin;
    private final double explosionPowerMax;
    private final ItemStack result;
    private final Identifier id;

    public BlastProcessorRecipe(Ingredient inputA, double explosionPowerMin, double explosionPowerMax, ItemStack result, Identifier id) {
        this.processingItem = inputA;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.result = result;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        return processingItem.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
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

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.result;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BlastProcessorRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return BlastProcessorRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<BlastProcessorRecipe> {
        private Type() {}
        public static final BlastProcessorRecipe.Type INSTANCE = new Type();
        public static final String ID = "blast_processing";
    }
}
