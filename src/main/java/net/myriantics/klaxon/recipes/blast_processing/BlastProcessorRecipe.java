package net.myriantics.klaxon.recipes.blast_processing;

import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.recipes.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.ItemExplosionPowerHelper;

import java.util.Optional;

import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.CATALYST_INDEX;
import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.PROCESS_ITEM_INDEX;

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
        return processingItem.test(inventory.getStack(PROCESS_ITEM_INDEX));
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

    public boolean isCompatibleWithCatalyst(double explosionPower) {
        return explosionPowerMin <= explosionPower && explosionPowerMax >= explosionPower;
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
        return KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.BLAST_PROCESSING;
    }

}
