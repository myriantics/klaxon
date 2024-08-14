package net.myriantics.klaxon.recipes.blast_processing;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.ItemExplosionPowerHelper;

import java.util.Optional;

import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.CATALYST_INDEX;
import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.PROCESS_ITEM_INDEX;

public class BlastProcessorRecipe implements Recipe<SimpleInventory> {
    private final Ingredient processingItem;
    private final double explosionPowerMin;
    private final double explosionPowerMax;
    private final boolean requiresFire;
    private final ItemStack result;
    private final Identifier id;

    private ItemStack catalystItem;
    private double explosionPower;
    private boolean producesFire;

    public BlastProcessorRecipe(Ingredient inputA, double explosionPowerMin, double explosionPowerMax, boolean requiresFire, ItemStack result, Identifier id) {
        this.processingItem = inputA;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.requiresFire = requiresFire;
        this.result = result;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        boolean matches = processingItem.test(inventory.getStack(PROCESS_ITEM_INDEX));

        if (matches) {
            Optional<ItemExplosionPowerRecipe> explosionPowerMatch = ItemExplosionPowerHelper.getExplosionPowerData(world, inventory.getStack(CATALYST_INDEX));

            if (explosionPowerMatch.isPresent()) {
                this.explosionPower = explosionPowerMatch.get().getExplosionPower();
                this.producesFire = explosionPowerMatch.get().producesFire();
                this.catalystItem = inventory.getStack(CATALYST_INDEX);
            } else {
                this.explosionPower = 0.0;
                this.producesFire = false;
                this.catalystItem = ItemStack.EMPTY;
            }
        }

        return matches;
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

    public ItemStack getCatalystItem() {
        return catalystItem;
    }

    public boolean requiresFire() {return requiresFire;}

    public double getExplosionPower() {
        return explosionPower;
    }

    public boolean producesFire() {
        return producesFire;
    }

    public enum outputState {
        MISSING_RECIPE,
        MISSING_FUEL,
        UNDERPOWERED,
        OVERPOWERED,
        MISSING_FIRE,
        SUCCESS;
    }

    public outputState getOutputState() {
        // is there fuel present
        if (explosionPower > 0) {
            if (explosionPower < explosionPowerMin) {
                return outputState.UNDERPOWERED;
                // PRESENT RECIPE BUT LOW-POWERED FUEL
            } else if(explosionPower > explosionPowerMax) {
                return outputState.OVERPOWERED;
                // PRESENT RECIPE BUT OVERPOWERED FUEL
            } else {
                if (requiresFire == producesFire || producesFire) {
                    return outputState.SUCCESS;
                    // PRESENT RECIPE AND VALID FUEL
                } else {
                    return outputState.MISSING_FIRE;
                    // INVALID RECIPE - REQUIRES FIRE
                }
            }
        }
        return outputState.MISSING_FUEL;
        // NO FUEL / INVALID FUEL
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
