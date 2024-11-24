package net.myriantics.klaxon.api.behavior;

import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.customblocks.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.recipes.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingOutputState;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class ItemBlastProcessorBehavior implements BlastProcessorBehavior {

    @Override
    public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData) {
        if (world != null) {
            BlockState activeBlockState = world.getBlockState(pos);
            if (activeBlockState.getBlock().equals(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)) {
                if (powerData.explosionPower() > 0.0) {
                    Direction direction = activeBlockState.get(Properties.FACING);
                    Position position = blastProcessor.getOutputLocation(direction);

                    blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
                    world.createExplosion(null, position.getX(), position.getY(), position.getZ(), (float) powerData.explosionPower(), powerData.producesFire(), World.ExplosionSourceType.BLOCK);
                    world.updateNeighbors(pos, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
                }
            }
        }
    }

    public void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ItemExplosionPowerData powerData) {
        if (world == null || blastProcessor.isEmpty()) {
            return;
        }

        Direction facing = world.getBlockState(pos).get(DeepslateBlastProcessorBlock.FACING);

        switch (recipeData.outputState()) {
            case MISSING_FUEL, MISSING_RECIPE -> {
                for (ItemStack ejectedStack : blastProcessor.getItems()) {
                    ItemDispenserBehavior.spawnItem(world, ejectedStack.copy(), 8, facing, blastProcessor.getOutputLocation(facing));
                }
                blastProcessor.clear();
            }
            case OVERPOWERED, UNDERPOWERED -> {
                blastProcessor.clear();
            }
            case SUCCESS -> {
                blastProcessor.clear();
                ItemDispenserBehavior.spawnItem(world, recipeData.result().copy(), 8, facing, blastProcessor.getOutputLocation(facing));
            }
        }
    }

    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, SimpleInventory recipeInventory) {
        RecipeManager recipeManager = world.getRecipeManager();

        Optional<ItemExplosionPowerRecipe> itemExplosionPowerMatch = Optional.empty();

        if (!recipeInventory.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).isEmpty()) {
            itemExplosionPowerMatch = recipeManager.getFirstMatch(KlaxonRecipeTypes.ITEM_EXPLOSION_POWER, new SimpleInventory(blastProcessor.getItems().get(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX)), world);
        }

        if (itemExplosionPowerMatch.isPresent()) {
            return new ItemExplosionPowerData(itemExplosionPowerMatch.get().getExplosionPower(), itemExplosionPowerMatch.get().producesFire());
        }
        return new ItemExplosionPowerData(0.0, false);
    }

    public BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, SimpleInventory recipeInventory, ItemExplosionPowerData powerData) {

        Optional<BlastProcessingRecipe> blastProcessingMatch = Optional.empty();

        if (!blastProcessor.getItems().get(DeepslateBlastProcessorBlockEntity.PROCESS_ITEM_INDEX).isEmpty()) {
            blastProcessingMatch = selectBlastProcessingRecipe(world, recipeInventory, powerData);
        }
        if (blastProcessingMatch.isPresent()) {
            BlastProcessingRecipe recipe = blastProcessingMatch.get();

            BlastProcessingOutputState interimOutputState = computeOutputState(powerData, recipe);

            return new BlastProcessingRecipeData(recipe.getExplosionPowerMin(), recipe.getExplosionPowerMax(), recipe.getOutput(world.getRegistryManager()), interimOutputState);
        } else {
            return new BlastProcessingRecipeData(0.0, 0.0, ItemStack.EMPTY, BlastProcessingOutputState.MISSING_RECIPE);
        }
    }

    private static @NotNull BlastProcessingOutputState computeOutputState(ItemExplosionPowerData powerData, BlastProcessingRecipe recipe) {
        double explosionPower = powerData.explosionPower();
        double explosionPowerMin = recipe.getExplosionPowerMin();
        double explosionPowerMax = recipe.getExplosionPowerMax();

        // determine output state
        BlastProcessingOutputState interimOutputState;
        if (explosionPower > 0) {
            if (explosionPower < explosionPowerMin) {
                interimOutputState = BlastProcessingOutputState.UNDERPOWERED;
            } else if (explosionPower > explosionPowerMax) {
                interimOutputState = BlastProcessingOutputState.OVERPOWERED;
            } else {
                interimOutputState = BlastProcessingOutputState.SUCCESS;
            }
        } else {
            interimOutputState = BlastProcessingOutputState.MISSING_FUEL;
        }
        return interimOutputState;
    }

    // defaults to showing recipe with the lowest explosion power, but will switch to higher explosion power recipe if lowest is invalid
    private Optional<BlastProcessingRecipe> selectBlastProcessingRecipe(World world, SimpleInventory recipeInventory, ItemExplosionPowerData powerData) {
        List<BlastProcessingRecipe> initialRecipes = world.getRecipeManager().getAllMatches(KlaxonRecipeTypes.BLAST_PROCESSING, recipeInventory, world);
        if (initialRecipes.isEmpty()) {
            return Optional.empty();
        }

        // add all matching recipes to one list
        DefaultedList<BlastProcessingRecipe> recipes = DefaultedList.of();
        recipes.addAll(initialRecipes);

        // sort all matching recipes by the lowest explosion power, counting up
        Comparator<BlastProcessingRecipe> byLowestExplosionPower = Comparator.comparing(BlastProcessingRecipe::getExplosionPowerMin);
        recipes.sort(byLowestExplosionPower);

        // if there's a catalyst, iterate through all matching recipes until you find the matching one with the least explosion power
        if (!recipeInventory.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).isEmpty()) {
           for (BlastProcessingRecipe activeRecipe : recipes) {
                if (activeRecipe.isCompatibleWithCatalyst(powerData.explosionPower())) {
                    return Optional.of(activeRecipe);
                }
            }
        }

        return Optional.of(recipes.get(0));
    }
}