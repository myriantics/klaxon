package net.myriantics.klaxon.recipes.blast_processing;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipes.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.CATALYST_INDEX;
import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.PROCESS_ITEM_INDEX;

public class BlastProcessingInator {

    private final SimpleInventory recipeInventory;
    private final double explosionPowerMin;
    private final double explosionPowerMax;
    private final double explosionPower;
    private final boolean producesFire;
    private final ItemStack result;
    private final BlastProcessorOutputState outputState;
    private final World world;

    public BlastProcessingInator(World world, Inventory inventory) {
        this.world = world;

        ItemStack processItem = inventory.getStack(PROCESS_ITEM_INDEX);
        ItemStack catalystItem = inventory.getStack(CATALYST_INDEX);

        this.recipeInventory = new SimpleInventory(inventory.size());

        for (int i = 0; i < inventory.size(); i++) {
            recipeInventory.setStack(i, inventory.getStack(i));
        }

        RecipeManager recipeManager = world.getRecipeManager();

        Optional<ItemExplosionPowerRecipe> itemExplosionPowerMatch = Optional.empty();
        Optional<BlastProcessorRecipe> blastProcessingMatch = Optional.empty();


        if (!catalystItem.isEmpty()) {
            itemExplosionPowerMatch = recipeManager.getFirstMatch(KlaxonRecipeTypes.ITEM_EXPLOSION_POWER, recipeInventory, world);
        }

        if (itemExplosionPowerMatch.isPresent()) {
            this.explosionPower = itemExplosionPowerMatch.get().getExplosionPower();
            this.producesFire = itemExplosionPowerMatch.get().producesFire();
        } else {
            this.explosionPower = 0.0;
            this.producesFire = false;
        }

        if (!processItem.isEmpty()) {
            blastProcessingMatch = getBlastProcessingRecipe();
        }

        BlastProcessorOutputState interimOutputState;

        if (blastProcessingMatch.isPresent()) {
            this.explosionPowerMin = blastProcessingMatch.get().getExplosionPowerMin();
            this.explosionPowerMax = blastProcessingMatch.get().getExplosionPowerMax();
            this.result = blastProcessingMatch.get().getOutput(world.getRegistryManager());
            if (explosionPower > 0) {
                if (explosionPower < explosionPowerMin) {
                    interimOutputState = BlastProcessorOutputState.UNDERPOWERED;
                } else if (explosionPower > explosionPowerMax) {
                    interimOutputState = BlastProcessorOutputState.OVERPOWERED;
                } else {
                    interimOutputState = BlastProcessorOutputState.SUCCESS;
                }
            } else {
                interimOutputState = BlastProcessorOutputState.MISSING_FUEL;
            }
        } else {
            this.explosionPowerMin = 0.0;
            this.explosionPowerMax = 0.0;
            this.result = ItemStack.EMPTY;
            interimOutputState = BlastProcessorOutputState.MISSING_RECIPE;
        }

        this.outputState = interimOutputState;
    }

    public double getExplosionPowerMin() {
        return explosionPowerMin;
    }

    public double getExplosionPowerMax() {
        return explosionPowerMax;
    }

    public double getExplosionPower() {
        return explosionPower;
    }

    public boolean producesFire() {
        return producesFire;
    }

    public ItemStack getResult() {
        return result;
    }

    public BlastProcessorOutputState getOutputState() {
        return outputState;
    }

    // defaults to showing recipe with lowest explosion power, but will switch to higher explosion power recipe if lowest is invalid
    private Optional<BlastProcessorRecipe> getBlastProcessingRecipe() {
        List<BlastProcessorRecipe> initialRecipes = world.getRecipeManager().getAllMatches(KlaxonRecipeTypes.BLAST_PROCESSING, recipeInventory, world);
        if (initialRecipes.isEmpty()) {
            return Optional.empty();
        }

        DefaultedList<BlastProcessorRecipe> recipes = DefaultedList.of();
        recipes.addAll(initialRecipes);

        Comparator<BlastProcessorRecipe> byLowestExplosionPower = Comparator.comparing(BlastProcessorRecipe::getExplosionPowerMin);
        recipes.sort(byLowestExplosionPower);

        if (recipeInventory.getStack(CATALYST_INDEX).isEmpty()) {
            return Optional.of(recipes.get(0));
        }

        for (BlastProcessorRecipe activeRecipe : recipes) {
            if (activeRecipe.isCompatibleWithCatalyst(explosionPower)) {
                return Optional.of(activeRecipe);
            }
        }

        return Optional.of(recipes.get(0));
    }
}
