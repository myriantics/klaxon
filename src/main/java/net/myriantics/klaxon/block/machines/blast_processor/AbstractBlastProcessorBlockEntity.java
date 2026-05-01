package net.myriantics.klaxon.block.machines.blast_processor;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.core.*;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
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
import net.myriantics.klaxon.block.KlaxonBaseSidedContainerBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipeLogic;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;
import net.myriantics.klaxon.util.container.SlotsWrapperContainer;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class AbstractBlastProcessorBlockEntity extends KlaxonBaseSidedContainerBlockEntity implements ExplosiveCatalystVessel {

    protected static final int INGREDIENT_INDEX = 0;
    protected static final int CATALYST_INDEX = 1;

    protected final SlotsWrapperContainer ingredientContainer = new SlotsWrapperContainer(this, INGREDIENT_INDEX);
    protected final SlotsWrapperContainer catalystContainer = new SlotsWrapperContainer(this, CATALYST_INDEX);


    protected AbstractBlastProcessorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    public float computeIngredientSlotFill() {
        return this.computeSlotFill(INGREDIENT_INDEX);
    }

    public float computeCatalystSlotFill() {
        return this.computeSlotFill(CATALYST_INDEX);
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
                KlaxonItemStackHelper.insertAndMerge(ejectedContents, contained.copy());
            }
            for (ItemStack ejected : ejectedContents) {
                this.ejectItem(ejected, facing);
            }
        }

        // blast processor will always be empty after actions have been performed
        this.clearContent();
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

    public BlastProcessingRecipeData getCraftedStacks(BlastProcessingRecipeInput input) {
        Level level = this.level;
        ExplosiveCatalystData catalystData = input.getCatalystData();
        if (level == null || this.getIngredientStack().isEmpty()) {
            return BlastProcessingRecipeData.ZERO;
        }

        Optional<BlastProcessingRecipe> match = selectBlastProcessingRecipe(level, input, input.getCatalystData());
        if (match.isPresent()) {
            BlastProcessingRecipe recipe = match.get();
            RandomSource random = level.getRandom();

            List<ItemStack> outputStacks = new ArrayList<>();

            ItemStack ingredient = this.getIngredientStack();

            for (int i = 0; i < ingredient.getCount(); i++) {
                for (ItemStack stack : recipe.craft(input, level.registryAccess(), random)) {
                    KlaxonItemStackHelper.insertAndMerge(outputStacks, stack);
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

    public BlastProcessingRecipeData getDisplayStacks(BlastProcessingRecipeInput input) {
        Level level = this.level;
        ExplosiveCatalystData catalystData = input.getCatalystData();
        if (level == null || this.getIngredientStack().isEmpty()) {
            return BlastProcessingRecipeData.ZERO;
        }

        Optional<BlastProcessingRecipe> match = selectBlastProcessingRecipe(level, input, input.getCatalystData());
        if (match.isPresent()) {
            BlastProcessingRecipe recipe = match.get();

            ItemStack[] rawOutput = recipe.getRecipeOutputCompound().getDisplayStacks();
            List<ItemStack> outputStacks = new ArrayList<>(rawOutput.length);

            for (int i = 0; i < rawOutput.length; i++) {
                outputStacks.set(i, catalystData.producesFire()
                        ? this.tryPerformBlastingSmelting(level, rawOutput[i])
                        : rawOutput[i]
                );
            }

            return new BlastProcessingRecipeData(outputStacks, recipe.getExplosionPowerMin(), recipe.getExplosionPowerMax());
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
        if (!this.getCatalystStack().isEmpty()) {
            for (BlastProcessingRecipe activeRecipe : recipes) {
                if (activeRecipe.isCompatibleWithCatalyst(powerData)) {
                    return Optional.of(activeRecipe);
                }
            }
        }

        return Optional.of(recipes.getFirst());
    }

    private void tryBlastingSmeltingRecipeOnAllStacks(List<ItemStack> stacks, Level level) {
        this.tryBlastingSmeltingRecipeOnAllStacks(
                stacks.size(),
                stacks::get,
                stacks::set,
                level
        );
    }

    private void tryBlastingSmeltingRecipeOnAllStacks(ItemStack[] stacks, Level level) {
        this.tryBlastingSmeltingRecipeOnAllStacks(
                stacks.length,
                integer -> stacks[integer],
                (integer, stack) -> stacks[integer] = stack,
                level
        );
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

    private void tryBlastingSmeltingRecipeOnAllStacks(int size, Int2ObjectFunction<ItemStack> getter, BiConsumer<Integer, ItemStack> setter, Level world) {
        for (int i = 0; i < size; i++) {
            ItemStack stack = getter.get(i);

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
                setter.accept(i,
                        blastingRecipe.get().value().assemble(
                                input,
                                world.registryAccess()
                        ).copyWithCount(stack.getCount())
                );
            }
        }
    }

    @Override
    public boolean shouldExposeExplosiveCatalystData() {
        return true;
    }

    @Override
    public ExplosiveCatalystData getEffectiveCatalystData() {
        return ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(this.getContext(), this.getCatalystStack());
    }

    @Override
    public ExplosiveCatalystData getRawData() {
        return ExplosiveCatalystDefinitionRecipeLogic.computeRawExplosiveCatalystData(this.getContext(), this.getCatalystStack());
    }

    public abstract Position getItemOutputLocation(Direction facing);

    public final ItemStack getIngredientStack() {
        return this.getItem(INGREDIENT_INDEX);
    }

    public final ItemStack getCatalystStack() {
        return this.getItem(CATALYST_INDEX);
    }

    public ExplosiveCatalystContext.Block getContext() {
        return new ExplosiveCatalystContext.Block(this.level, this.getCatalystStack().getComponents(), this.getBlockPos());
    }
}
