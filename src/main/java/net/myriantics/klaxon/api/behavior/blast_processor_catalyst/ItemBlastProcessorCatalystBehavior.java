package net.myriantics.klaxon.api.behavior.blast_processor_catalyst;

import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.minecraft.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.registry.minecraft.KlaxonGamerules;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingOutputState;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class ItemBlastProcessorCatalystBehavior implements BlastProcessorCatalystBehavior {
    private final Identifier id;

    public ItemBlastProcessorCatalystBehavior(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData) {
        if (world instanceof ServerWorld serverWorld) {
            BlockState activeBlockState = world.getBlockState(pos);
            if (activeBlockState.getBlock().equals(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)) {
                if (powerData.explosionPower() > 0.0) {
                    Direction direction = activeBlockState.get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING);
                    Position position = blastProcessor.getExplosionOutputLocation(direction);

                    // make sure we're actually allowed to modify world
                    boolean shouldModifyWorld = serverWorld.getGameRules().getBoolean(KlaxonGamerules.SHOULD_BLAST_PROCESSOR_EXPLOSION_MODIFY_WORLD);

                    blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
                    serverWorld.createExplosion(null, null,
                            // this is used to differentiate blast processor explosions from normal ones
                            new BlastProcessorExplosionBehavior(shouldModifyWorld),
                            position.getX(), position.getY(), position.getZ(),
                            (float) powerData.explosionPower(),
                            shouldModifyWorld && powerData.producesFire(),
                            World.ExplosionSourceType.BLOCK,
                            ParticleTypes.EXPLOSION,
                            ParticleTypes.EXPLOSION_EMITTER,
                            SoundEvents.ENTITY_GENERIC_EXPLODE);
                    serverWorld.updateNeighbors(pos, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
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
                if (powerData.explosionPower() <= 0) {
                    for (ItemStack ejectedStack : blastProcessor.getItems()) {
                        ItemDispenserBehavior.spawnItem(world, ejectedStack.copy(), 8, facing, blastProcessor.getItemOutputLocation(facing));
                    }
                }
            }
            case OVERPOWERED, UNDERPOWERED -> {
            }
            case SUCCESS -> {
                Position itemOutputPos = blastProcessor.getItemOutputLocation(facing);
                double advancementGrantRange = 17.0;

                ItemStack outputStack = recipeData.result().copy();

                // if the catalyst produces fire, try to smelt output stack as if it were in a blast furnace
                if (powerData.producesFire()) {
                    SingleStackRecipeInput blastingRecipeInput = new SingleStackRecipeInput(outputStack);
                    Optional<RecipeEntry<BlastingRecipe>> blastingRecipe = world.getRecipeManager().getFirstMatch(RecipeType.BLASTING, blastingRecipeInput, world);
                    if (blastingRecipe.isPresent()) {
                        outputStack = blastingRecipe.get().value().craft(blastingRecipeInput, world.getRegistryManager()).copyWithCount(outputStack.getCount());
                    }
                }

                ItemDispenserBehavior.spawnItem(world, outputStack, 8, facing, itemOutputPos);

                // proc blast processor crafting advancement
                for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, Box.of((Vec3d) itemOutputPos, advancementGrantRange, advancementGrantRange, advancementGrantRange))) {
                    KlaxonAdvancementTriggers.triggerBlockActivation(serverPlayerEntity, world.getBlockState(pos));
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

            return new BlastProcessingRecipeData(recipe.getExplosionPowerMin(), recipe.getExplosionPowerMax(), recipe.craft(recipeInventory, world.getRegistryManager()), interimOutputState);
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
    public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory) {
        return true;
    }
}