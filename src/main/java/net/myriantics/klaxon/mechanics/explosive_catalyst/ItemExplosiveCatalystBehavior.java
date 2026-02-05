package net.myriantics.klaxon.mechanics.explosive_catalyst;

import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class ItemExplosiveCatalystBehavior implements ExplosiveCatalystBehavior {
    private final Identifier id;

    public ItemExplosiveCatalystBehavior(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData powerData, boolean shouldModifyWorld) {
        if (world instanceof ServerWorld serverWorld) {
            BlockState activeBlockState = world.getBlockState(pos);
            if (activeBlockState.getBlock().equals(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)) {
                if (powerData.explosionPower() > 0.0) {
                    Direction direction = activeBlockState.get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING);
                    Position position = blastProcessor.getExplosionOutputLocation(direction);

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
                    serverWorld.updateNeighbors(pos, activeBlockState.getBlock());
                }
            }
        }
    }

    public void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ExplosiveCatalystData powerData) {
        if (world == null || blastProcessor.isEmpty()) {
            return;
        }

        Direction facing = world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING);

        if (recipeData.outputStacks().length == 0) {
            if (powerData.explosionPower() <= 0 || powerData.explosionPower() < recipeData.explosionPowerMin()) {
                for (ItemStack ejectedStack : blastProcessor.getHeldStacks()) {
                    ItemDispenserBehavior.spawnItem(world, ejectedStack.copy(), 8, facing, blastProcessor.getItemOutputLocation(facing));
                }
            }
        } else {
            Position itemOutputPos = blastProcessor.getItemOutputLocation(facing);
            double advancementGrantRange = 17.0;

            for (ItemStack stack : recipeData.outputStacks()) {
                if (!stack.isEmpty()) {
                    ItemDispenserBehavior.spawnItem(world, stack, 8, facing, itemOutputPos);
                }
            }

            // proc blast processor crafting advancement
            for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, Box.of((Vec3d) itemOutputPos, advancementGrantRange, advancementGrantRange, advancementGrantRange))) {
                KlaxonAdvancementTriggers.triggerBlockActivation(serverPlayerEntity, world.getBlockState(pos));
            }
        }

        // blast processor will always be empty after actions have been performed
        blastProcessor.clear();
    }

    public ExplosiveCatalystData transformExplosiveCatalystData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data) {
        return data;
    }

    public BlastProcessingRecipeData getBlastProcessingPreviewData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory) {
        Optional<BlastProcessingRecipe> blastProcessingMatch = Optional.empty();
        ExplosiveCatalystData powerData = recipeInventory.getPowerData();

        if (!recipeInventory.getStackInSlot(DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX).isEmpty()) {
            blastProcessingMatch = selectBlastProcessingRecipe(world, recipeInventory, powerData);
        }
        if (blastProcessingMatch.isPresent()) {
            BlastProcessingRecipe recipe = blastProcessingMatch.get();

            ItemStack[] outputStacks = recipe.getRecipeOutputCompound().getDisplayStacks();

            // if the catalyst produces fire, try to smelt output stacks as if they were in a blast furnace
            if (powerData.producesFire()) {
                tryBlastingSmeltingRecipeOnAllStacks(outputStacks, world);
            }

            return new BlastProcessingRecipeData(recipe.getExplosionPowerMin(), recipe.getExplosionPowerMax(), outputStacks);
        } else {
            return BlastProcessingRecipeData.ZERO;
        }
    }

    public BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory) {
        Optional<BlastProcessingRecipe> blastProcessingMatch = Optional.empty();
        ExplosiveCatalystData powerData = recipeInventory.getPowerData();

        if (!recipeInventory.getStackInSlot(DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX).isEmpty()) {
            blastProcessingMatch = selectBlastProcessingRecipe(world, recipeInventory, powerData);
        }
        if (blastProcessingMatch.isPresent()) {
            BlastProcessingRecipe recipe = blastProcessingMatch.get();

            ItemStack[] outputStacks = recipe.craft(recipeInventory, world.getRegistryManager(), world.getRandom());

            // if the catalyst produces fire, try to smelt output stacks as if they were in a blast furnace
            if (powerData.producesFire()) {
                tryBlastingSmeltingRecipeOnAllStacks(outputStacks, world);
            }

            return new BlastProcessingRecipeData(recipe.getExplosionPowerMin(), recipe.getExplosionPowerMax(), outputStacks);
        } else {
            return BlastProcessingRecipeData.ZERO;
        }
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    // defaults to showing recipe with the lowest explosion power, but will switch to higher explosion power recipe if lowest is invalid
    private Optional<BlastProcessingRecipe> selectBlastProcessingRecipe(World world, BlastProcessingRecipeInput recipeInventory, ExplosiveCatalystData powerData) {
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
                if (activeRecipe.isCompatibleWithCatalyst(powerData)) {
                    return Optional.of(activeRecipe);
                }
            }
        }

        return Optional.of(recipes.getFirst());
    }

    @Override
    public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystDefinitionRecipeInput recipeInventory) {
        return true;
    }

    private void tryBlastingSmeltingRecipeOnAllStacks(ItemStack[] stacks, World world) {
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];

            // init recipe input
            SingleStackRecipeInput input = new SingleStackRecipeInput(stack);

            // find blasting recipe
            Optional<RecipeEntry<BlastingRecipe>> blastingRecipe = world.getRecipeManager().getFirstMatch(
                    RecipeType.BLASTING,
                    input,
                    world
            );

            // if recipe was successful, overwrite that index in output stacks with proper count
            if (blastingRecipe.isPresent()) {
                stacks[i] = blastingRecipe.get().value().craft(
                        input,
                        world.getRegistryManager()
                ).copyWithCount(stack.getCount());
            }
        }
    }
}