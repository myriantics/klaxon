package net.myriantics.klaxon.api.behavior;

import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.myriantics.klaxon.advancement.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.customblocks.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingOutputState;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class ItemBlastProcessorBehavior implements BlastProcessorBehavior {

    @Override
    public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData, boolean isMuffled) {
        if (world != null) {
            BlockState activeBlockState = world.getBlockState(pos);
            if (activeBlockState.getBlock().equals(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)) {
                if (powerData.explosionPower() > 0.0) {
                    Direction direction = activeBlockState.get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING);
                    Position position = blastProcessor.getExplosionOutputLocation(direction);

                    blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
                    world.createExplosion(null, null,
                            // this is used to differentiate blast processor explosions from normal ones
                            new ExplosionBehavior(),
                            position.getX(), position.getY(), position.getZ(),
                            (float) powerData.explosionPower(),
                            powerData.producesFire(),
                            World.ExplosionSourceType.BLOCK,
                            ParticleTypes.EXPLOSION,
                            ParticleTypes.EXPLOSION_EMITTER,
                            // if block is muffled, don't produce explosion sound
                            isMuffled ? RegistryEntry.of(SoundEvents.INTENTIONALLY_EMPTY) : SoundEvents.ENTITY_GENERIC_EXPLODE);
                    world.updateNeighbors(pos, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
                }
            }
        }
    }

    public void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ItemExplosionPowerData powerData) {
        if (world == null || blastProcessor.isEmpty()) {
            return;
        }

        Direction facing = world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING);

        switch (recipeData.outputState()) {
            case MISSING_FUEL, MISSING_RECIPE -> {
                for (ItemStack ejectedStack : blastProcessor.getItems()) {
                    ItemDispenserBehavior.spawnItem(world, ejectedStack.copy(), 8, facing, blastProcessor.getItemOutputLocation(facing));
                }
            }
            case OVERPOWERED, UNDERPOWERED -> {
            }
            case SUCCESS -> {
                Position itemOutputPos = blastProcessor.getItemOutputLocation(facing);
                double advancementGrantRange = 17.0;

                ItemDispenserBehavior.spawnItem(world, recipeData.result().copy(), 8, facing, itemOutputPos);

                // proc blast processor crafting advancement
                for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, Box.of((Vec3d) itemOutputPos, advancementGrantRange, advancementGrantRange, advancementGrantRange))) {
                    KlaxonAdvancementCriteria.BLOCK_ACTIVATION_CRITERION.trigger(serverPlayerEntity, world.getBlockState(pos));
                }
            }
        }

        // blast processor will always be empty after actions have been performed
        blastProcessor.clear();
    }

    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory) {
        RecipeManager recipeManager = world.getRecipeManager();

        Optional<RecipeEntry<ItemExplosionPowerRecipe>> itemExplosionPowerMatch = Optional.empty();

        if (!recipeInventory.getStackInSlot(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).isEmpty()) {
            itemExplosionPowerMatch = recipeManager.getFirstMatch(KlaxonRecipeTypes.ITEM_EXPLOSION_POWER, recipeInventory, world);
        }

        if (itemExplosionPowerMatch.isPresent()) {
            return new ItemExplosionPowerData(itemExplosionPowerMatch.get().value().getExplosionPower(), itemExplosionPowerMatch.get().value().producesFire());
        }
        return new ItemExplosionPowerData(0.0, false);
    }

    public BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory, ItemExplosionPowerData powerData) {

        Optional<BlastProcessingRecipe> blastProcessingMatch = Optional.empty();

        if (!recipeInventory.getStackInSlot(DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX).isEmpty()) {
            blastProcessingMatch = selectBlastProcessingRecipe(world, recipeInventory, powerData);
        }
        if (blastProcessingMatch.isPresent()) {
            BlastProcessingRecipe recipe = blastProcessingMatch.get();

            BlastProcessingOutputState interimOutputState = computeOutputState(powerData, recipe);

            return new BlastProcessingRecipeData(recipe.getExplosionPowerMin(), recipe.getExplosionPowerMax(), recipe.getResult(world.getRegistryManager()), interimOutputState);
        } else {
            return new BlastProcessingRecipeData(0.0, 0.0, ItemStack.EMPTY, BlastProcessingOutputState.MISSING_RECIPE);
        }
    }

    @Override
    public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
        return null;
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
    private Optional<BlastProcessingRecipe> selectBlastProcessingRecipe(World world, RecipeInput recipeInventory, ItemExplosionPowerData powerData) {
        List<RecipeEntry<BlastProcessingRecipe>> initialRecipes = world.getRecipeManager().getAllMatches(KlaxonRecipeTypes.BLAST_PROCESSING, recipeInventory, world);
        if (initialRecipes.isEmpty()) {
            return Optional.empty();
        }

        // add all matching recipes to one list
        DefaultedList<BlastProcessingRecipe> recipes = DefaultedList.of();
        for (RecipeEntry<BlastProcessingRecipe> recipeEntry : initialRecipes) {
            recipes.add(recipeEntry.value());
        }

        // sort all matching recipes by the lowest explosion power, counting up
        Comparator<BlastProcessingRecipe> byLowestExplosionPower = Comparator.comparing(BlastProcessingRecipe::getExplosionPowerMin);
        recipes.sort(byLowestExplosionPower);

        // if there's a catalyst, iterate through all matching recipes until you find the matching one with the least explosion power
        if (!recipeInventory.getStackInSlot(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).isEmpty()) {
           for (BlastProcessingRecipe activeRecipe : recipes) {
                if (activeRecipe.isCompatibleWithCatalyst(powerData.explosionPower())) {
                    return Optional.of(activeRecipe);
                }
            }
        }

        return Optional.of(recipes.getFirst());
    }

    @Override
    public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory, boolean isMuffled) {
        return true;
    }
}