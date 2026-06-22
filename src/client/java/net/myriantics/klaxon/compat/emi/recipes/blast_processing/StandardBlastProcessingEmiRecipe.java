package net.myriantics.klaxon.compat.emi.recipes.blast_processing;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.compat.emi.recipes.AbstractBlastProcessingEmiRecipe;
import net.myriantics.klaxon.recipe.blast_processing.StandardBlastProcessingRecipe;

import java.util.ArrayList;
import java.util.List;

public class StandardBlastProcessingEmiRecipe extends AbstractBlastProcessingEmiRecipe<StandardBlastProcessingRecipe> {

    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputStacks;

    public StandardBlastProcessingEmiRecipe(StandardBlastProcessingRecipe recipe, ResourceLocation id) {
        super(recipe, id);
        this.inputs = List.of(EmiIngredient.of(recipe.getIngredient()));
        List<EmiStack> outputStacks = new ArrayList<>();
        for (ItemStack stack : recipe.getRecipeOutputCompound().getDisplayStacks()) {
            outputStacks.add(EmiStack.of(stack));
        }
        this.outputStacks = outputStacks;
    }

    @Override
    protected SlotWidget addInputSlot(WidgetHolder widgets, int x, int y) {
        return widgets.addSlot(this.inputs.getFirst(), x, y);
    }

    @Override
    protected SlotWidget addResultSlot(WidgetHolder widgets, int index, int x, int y) {
        return widgets.addSlot(index < outputStacks.size() ? outputStacks.get(index) : EmiStack.EMPTY, x, y);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputStacks;
    }
}
