package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractToolUsageEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> requiredTool;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public AbstractToolUsageEmiRecipe(RecipeHolder<ToolUsageRecipe> recipe, EmiIngredient requiredTool) {
        this.id = recipe.id();
        this.requiredTool = List.of(requiredTool);
        this.input = List.of(EmiIngredient.of(recipe.value().getInputIngredient()));
        this.output = List.of(EmiStack.of(recipe.value().getResultItem(null)));
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 76;
    }

    @Override
    public int getDisplayHeight() {
        return 45;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(input.get(0), 0, 9).appendTooltip(Component.translatable("klaxon.emi.text.tool_usage.dropped_item"));

        widgets.addSlot(getCatalysts().get(0), 29, 0).appendTooltip(Component.translatable("klaxon.emi.text.tool_usage.tool")).appendTooltip(Component.translatable("klaxon.emi.text.tool_usage.use"));

        widgets.addSlot(output.get(0), 58, 9).recipeContext(this);

        // todo: add dropped item animation here (maybe an accompanying hammer swinging one as well)

        widgets.addText(Component.translatable("klaxon.emi.text.tool_usage.use_compact"), 0, 38, 4210752, false);
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return requiredTool;
    }
}
