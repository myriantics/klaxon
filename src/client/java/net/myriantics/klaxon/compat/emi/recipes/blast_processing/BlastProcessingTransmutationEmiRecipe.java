package net.myriantics.klaxon.compat.emi.recipes.blast_processing;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.compat.emi.recipes.AbstractBlastProcessingEmiRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingTransmutationRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlastProcessingTransmutationEmiRecipe extends AbstractBlastProcessingEmiRecipe<BlastProcessingTransmutationRecipe> {

    private final Ingredient validStacks;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public BlastProcessingTransmutationEmiRecipe(BlastProcessingTransmutationRecipe recipe, ResourceLocation id) {
        super(recipe, id);
        this.validStacks = recipe.getIngredient();
        this.inputs = List.of(EmiIngredient.of(this.validStacks));
        List<EmiStack> outputs = new ArrayList<>();
        for (ItemStack stack : this.validStacks.getItems()) {
            outputs.add(EmiStack.of(stack));
        }
        this.outputs = outputs;
    }

    @Override
    protected SlotWidget addInputSlot(WidgetHolder widgets, int x, int y) {
        return new GeneratedSlotWidget(
                random -> this.outputs.get(random.nextInt(this.outputs.size())),
                this.unique,
                x, y
        );
    }

    @Override
    protected SlotWidget addResultSlot(WidgetHolder widgets, int index, int x, int y) {
        if (index == 0) {
            return new GeneratedSlotWidget(
                    random -> {
                        ItemStack[] stacks = this.validStacks.getItems();
                        ItemStack stack = stacks[random.nextInt(stacks.length)];
                        this.recipe.addDisplayComponents(stack);
                        return EmiStack.of(stack);
                    },
                    this.unique,
                    x, y
            );
        } else {
            return new SlotWidget(
                    EmiStack.EMPTY,
                    x, y
            );
        }
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
