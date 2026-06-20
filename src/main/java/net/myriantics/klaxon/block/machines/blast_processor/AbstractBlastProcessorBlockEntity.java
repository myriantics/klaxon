package net.myriantics.klaxon.block.machines.blast_processor;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystContextParams;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;
import net.myriantics.klaxon.util.container.KlaxonBaseSidedContainerBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;
import net.myriantics.klaxon.util.container.ContainerPartition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractBlastProcessorBlockEntity extends KlaxonBaseSidedContainerBlockEntity implements ExplosiveCatalystVessel {

    protected ContainerPartition ingredientPartition;
    protected ContainerPartition catalystPartition;

    protected AbstractBlastProcessorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        if (this.ingredientPartition == null) {
            throw new IllegalStateException("Ingredient Partition may not be null after initialization!");
        }
        if (this.catalystPartition == null) {
            throw new IllegalStateException("Catalyst Partition may not be null after initialization!");
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    public ContainerPartition getCatalystPartition() {
        return this.catalystPartition;
    }

    public ContainerPartition getIngredientPartition() {
        return this.ingredientPartition;
    }

    public abstract void redstoneTrigger();

    public abstract Direction getFacing();

    protected void ejectItems(BlastProcessingRecipeData recipeData, ExplosiveCatalystData catalystData) {
        Level level = this.level;
        if (level == null) {
            return;
        }

        Direction facing = this.getFacing();

        if (recipeData.success()) {
            Position itemOutputPos = this.getItemOutputLocation(facing);
            final double advancementGrantRange = 17.0;

            for (ItemStack stack : recipeData.outputStacks()) {
                if (!stack.isEmpty()) {
                    this.ejectItem(stack, facing);
                }
            }

            // proc blast processor crafting advancement
            for (ServerPlayer serverPlayerEntity : level.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize((Vec3) itemOutputPos, advancementGrantRange, advancementGrantRange, advancementGrantRange))) {
                KlaxonAdvancementTriggers.triggerBlockActivation(serverPlayerEntity, this.getBlockState());
            }
        } else if (catalystData.explosionPower() <= 0 || catalystData.explosionPower() < recipeData.explosionPowerMin()) {
            List<ItemStack> ejectedContents = new ArrayList<>(this.getContainerSize());
            for (ItemStack contained : this.getItems()) {
                KlaxonItemStackHelper.insertAndMergeAndAdd(ejectedContents, contained.copy());
            }
            for (ItemStack ejected : ejectedContents) {
                this.ejectItem(ejected, facing);
            }
        }

        // blast processor will always be empty after actions have been performed
        this.clearContent();
        this.setChanged();
    }

    /**
     * @param stack Assumed to have already been copied
     * @param facing So we don't have to re-poll the getter every time
     */
    protected void ejectItem(ItemStack stack, Direction facing) {
        if (this.level != null) {
            DefaultDispenseItemBehavior.spawnItem(this.level, stack, 8, facing, this.getItemOutputLocation(facing));
        }
    }

    public ExplosiveCatalystData adaptEffectiveDataForCrafting(ServerLevel serverLevel, ExplosiveCatalystData data, @Nullable Holder<ExplosiveCatalystBehavior> behavior, @Nullable ExplosiveCatalystContext context) {
        if (behavior == null) {
            behavior = data.behavior(serverLevel);
        }
        if (behavior.is(KlaxonExplosiveCatalystBehaviorTags.UNUSABLE_FOR_BLAST_PROCESSING)) {
            return ExplosiveCatalystData.ZERO;
        } else {
            return behavior.value().transformExplosiveCatalystData(context == null ? this.getContext(serverLevel) : context, data);
        }
    }

    public @NotNull Pair<ExplosiveCatalystData, Holder<ExplosiveCatalystBehavior>> findEffectiveCatalystData(ServerLevel serverLevel, @Nullable ExplosiveCatalystContext context) {
        @Nullable ExplosiveCatalystData effective = ExplosiveCatalystData.findEffective(
                context == null ? this.getContext(serverLevel) : context,
                this.getCatalystStack()
        );
        if (effective == null) {
            return new Pair<>(ExplosiveCatalystData.ZERO, ExplosiveCatalystData.ZERO.behavior(serverLevel));
        } else {
            return new Pair<>(effective, effective.behavior(serverLevel));
        }
    }

    public BlastProcessingRecipeData getCraftedStacks(BlastProcessingRecipeInput input) {
        Level level = this.level;
        ExplosiveCatalystData catalystData = input.getCatalystData();
        if (level == null || input.getIngredientStack().isEmpty() || this.getCatalystStack().isEmpty()) {
            return BlastProcessingRecipeData.ZERO;
        }

        Optional<BlastProcessingRecipe> match = selectBlastProcessingRecipe(level, input, input.getCatalystData());
        if (match.isPresent()) {
            BlastProcessingRecipe recipe = match.get();
            RandomSource random = level.getRandom();

            List<ItemStack> outputStacks = new ArrayList<>();

            ItemStack ingredient = this.getIngredientStack();

            for (int i = 0; i < ingredient.getCount(); i++) {
                for (ItemStack stack : recipe.properlyAssemble(input, level.registryAccess())) {
                    KlaxonItemStackHelper.insertAndMergeAndAdd(outputStacks, stack);
                }
            }

            // if the catalyst produces fire, try to smelt output stacks as if they were in a blast furnace
            if (catalystData.producesFire()) {
                for (int i = 0; i < outputStacks.size(); i++) {
                    outputStacks.set(i, this.tryPerformBlastingSmelting(level, outputStacks.get(i)));
                }
            }

            return new BlastProcessingRecipeData(outputStacks, recipe.getExplosionPowerMin(), recipe.getExplosionPowerMax());
        } else {
            return BlastProcessingRecipeData.ZERO;
        }
    }

    public @Nullable BlastProcessingRecipeData getDisplayStacks(BlastProcessingRecipeInput input) {
        Level level = this.level;
        ExplosiveCatalystData catalystData = input.getCatalystData();
        if (level == null || this.getIngredientStack().isEmpty()) {
            return null;
        }

        Optional<BlastProcessingRecipe> match = selectBlastProcessingRecipe(level, input, input.getCatalystData());
        if (match.isPresent()) {
            BlastProcessingRecipe recipe = match.get();

            ItemStack[] rawOutput = recipe.getDisplayStacks(input, level.registryAccess());
            List<ItemStack> outputStacks = new ArrayList<>(rawOutput.length);

            for (ItemStack stack : rawOutput) {
                outputStacks.add(catalystData.producesFire()
                        ? this.tryPerformBlastingSmelting(level, stack)
                        : stack
                );
            }

            return new BlastProcessingRecipeData(outputStacks, recipe.getExplosionPowerMin(), recipe.getExplosionPowerMax());
        } else {
            return null;
        }
    }

    // defaults to showing recipe with the lowest explosion power, but will switch to higher explosion power recipe if lowest is invalid
    private Optional<BlastProcessingRecipe> selectBlastProcessingRecipe(Level world, BlastProcessingRecipeInput recipeInventory, ExplosiveCatalystData powerData) {
        List<RecipeHolder<BlastProcessingRecipe>> initialRecipes = world.getRecipeManager().getRecipesFor(KlaxonRecipeTypes.BLAST_PROCESSING.value(), recipeInventory, world);
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
        if (!this.getCatalystStack().isEmpty()) {
            for (BlastProcessingRecipe activeRecipe : recipes) {
                if (activeRecipe.getBounds().matches(powerData.explosionPower())) {
                    return Optional.of(activeRecipe);
                }
            }
        }

        return Optional.of(recipes.getFirst());
    }

    private ItemStack tryPerformBlastingSmelting(Level level, ItemStack stack) {
        SingleRecipeInput input = new SingleRecipeInput(stack);

        Optional<RecipeHolder<BlastingRecipe>> blastingRecipe = level.getRecipeManager().getRecipeFor(
                RecipeType.BLASTING,
                input,
                level
        );

        if (blastingRecipe.isPresent()) {
            ItemStack result = blastingRecipe.get().value().assemble(input, level.registryAccess());

            // we gotta set count and preserve components here because blasting strips them
            result.applyComponents(stack.getComponents());
            result.setCount(stack.getCount());
            return result;
        } else {
            return stack;
        }
    }

    @Override
    public ExplosiveCatalystData getEffectiveCatalystData() {
        return this.level instanceof ServerLevel serverLevel
                ? ExplosiveCatalystData.findEffective(this.getContext(serverLevel), this.getCatalystStack())
                : ExplosiveCatalystData.ZERO;
    }

    @Override
    public ExplosiveCatalystData getRawData() {
        if (this.level instanceof ServerLevel serverLevel) {
            @Nullable ExplosiveCatalystData data = ExplosiveCatalystData.findRaw(serverLevel, this.getCatalystStack());
            if (data != null) {
                return data;
            }
        }

        return ExplosiveCatalystData.ZERO;
    }

    public abstract Position getItemOutputLocation(Direction facing);

    public final ItemStack getIngredientStack() {
        return this.ingredientPartition.getFirstNonEmptyStack();
    }

    public final ItemStack getCatalystStack() {
        return this.catalystPartition.getFirstNonEmptyStack();
    }

    public ExplosiveCatalystContext getContext(ServerLevel serverLevel) {
        return new ExplosiveCatalystContext(serverLevel, this.getCatalystStack().getComponents())
                .add(KlaxonExplosiveCatalystContextParams.SUPPORT_STATE, serverLevel.getBlockState(this.getBlockPos().below()))
                .add(KlaxonExplosiveCatalystContextParams.BLOCK_POS, this.getBlockPos());
    }
}
