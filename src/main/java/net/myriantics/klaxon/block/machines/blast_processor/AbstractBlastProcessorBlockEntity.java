package net.myriantics.klaxon.block.machines.blast_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class AbstractBlastProcessorBlockEntity extends RandomizableContainerBlockEntity {
    protected AbstractBlastProcessorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected void ejectItems(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ExplosiveCatalystData powerData) {
        if (world == null || blastProcessor.isEmpty()) {
            return;
        }

        Direction facing = world.getBlockState(pos).getValue(DeepslateBlastProcessorBlock.HORIZONTAL_FACING);

        if (recipeData.outputStacks().length == 0) {
            if (powerData.explosionPower() <= 0 || powerData.explosionPower() < recipeData.explosionPowerMin()) {
                for (ItemStack ejectedStack : blastProcessor.getItems()) {
                    DefaultDispenseItemBehavior.spawnItem(world, ejectedStack.copy(), 8, facing, blastProcessor.getItemOutputLocation(facing));
                }
            }
        } else {
            Position itemOutputPos = blastProcessor.getItemOutputLocation(facing);
            double advancementGrantRange = 17.0;

            for (ItemStack stack : recipeData.outputStacks()) {
                if (!stack.isEmpty()) {
                    DefaultDispenseItemBehavior.spawnItem(world, stack, 8, facing, itemOutputPos);
                }
            }

            // proc blast processor crafting advancement
            for (ServerPlayer serverPlayerEntity : world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize((Vec3) itemOutputPos, advancementGrantRange, advancementGrantRange, advancementGrantRange))) {
                KlaxonAdvancementTriggers.triggerBlockActivation(serverPlayerEntity, world.getBlockState(pos));
            }
        }

        // blast processor will always be empty after actions have been performed
        blastProcessor.clearContent();
    }

    public BlastProcessingRecipeData getBlastProcessingPreviewData(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory) {
        Optional<BlastProcessingRecipe> blastProcessingMatch = Optional.empty();
        ExplosiveCatalystData powerData = recipeInventory.getPowerData();

        if (!recipeInventory.getItem(DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX).isEmpty()) {
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

    public BlastProcessingRecipeData getBlastProcessingRecipeData(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory) {
        Optional<BlastProcessingRecipe> blastProcessingMatch = Optional.empty();
        ExplosiveCatalystData powerData = recipeInventory.getPowerData();

        if (!recipeInventory.getItem(DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX).isEmpty() && !powerData.behavior().is(KlaxonExplosiveCatalystBehaviorTags.UNUSABLE_FOR_CRAFTING)) {
            blastProcessingMatch = selectBlastProcessingRecipe(world, recipeInventory, powerData);
        }
        if (blastProcessingMatch.isPresent()) {
            BlastProcessingRecipe recipe = blastProcessingMatch.get();

            ItemStack[] outputStacks = recipe.craft(recipeInventory, world.registryAccess(), world.getRandom());

            // if the catalyst produces fire, try to smelt output stacks as if they were in a blast furnace
            if (powerData.producesFire()) {
                tryBlastingSmeltingRecipeOnAllStacks(outputStacks, world);
            }

            return new BlastProcessingRecipeData(recipe.getExplosionPowerMin(), recipe.getExplosionPowerMax(), outputStacks);
        } else {
            return BlastProcessingRecipeData.ZERO;
        }
    }

    // defaults to showing recipe with the lowest explosion power, but will switch to higher explosion power recipe if lowest is invalid
    private Optional<BlastProcessingRecipe> selectBlastProcessingRecipe(Level world, BlastProcessingRecipeInput recipeInventory, ExplosiveCatalystData powerData) {
        List<RecipeHolder<BlastProcessingRecipe>> initialRecipes = world.getRecipeManager().getRecipesFor(KlaxonRecipeTypes.BLAST_PROCESSING, recipeInventory, world);
        if (initialRecipes.isEmpty()) {
            return Optional.empty();
        }

        // add all matching recipes to one list
        NonNullList<BlastProcessingRecipe> recipes = NonNullList.create();
        for (RecipeHolder<BlastProcessingRecipe> recipeEntry : initialRecipes) {
            recipes.add(recipeEntry.value());
        }

        // sort all matching recipes by the lowest explosion power, counting up
        Comparator<BlastProcessingRecipe> byLowestExplosionPower = Comparator.comparing(BlastProcessingRecipe::getExplosionPowerMin);
        recipes.sort(byLowestExplosionPower);

        // if there's a catalyst, iterate through all matching recipes until you find the matching one with the least explosion power
        if (!recipeInventory.getItem(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).isEmpty()) {
            for (BlastProcessingRecipe activeRecipe : recipes) {
                if (activeRecipe.isCompatibleWithCatalyst(powerData)) {
                    return Optional.of(activeRecipe);
                }
            }
        }

        return Optional.of(recipes.getFirst());
    }

    private void tryBlastingSmeltingRecipeOnAllStacks(ItemStack[] stacks, Level world) {
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];

            // init recipe input
            SingleRecipeInput input = new SingleRecipeInput(stack);

            // find blasting recipe
            Optional<RecipeHolder<BlastingRecipe>> blastingRecipe = world.getRecipeManager().getRecipeFor(
                    RecipeType.BLASTING,
                    input,
                    world
            );

            // if recipe was successful, overwrite that index in output stacks with proper count
            if (blastingRecipe.isPresent()) {
                stacks[i] = blastingRecipe.get().value().assemble(
                        input,
                        world.registryAccess()
                ).copyWithCount(stack.getCount());
            }
        }
    }

    public abstract ItemStack getCatalystStack();

    public ExplosiveCatalystContext.Block getContext() {
        return new ExplosiveCatalystContext.Block(this.level, this.getCatalystStack().getComponents(), this.getBlockPos());
    }
}
