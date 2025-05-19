package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToolUsageEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> requiredTool;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public ToolUsageEmiRecipe(RecipeEntry<ToolUsageRecipe> recipe) {
        this.id = recipe.id();
        this.requiredTool = List.of(EmiIngredient.of(recipe.value().getRequiredTool()));
        this.input = List.of(EmiIngredient.of(recipe.value().getInputIngredient()));
        this.output = List.of(EmiStack.of(recipe.value().getResult(null)));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.TOOL_USAGE;
    }

    @Override
    public @Nullable Identifier getId() {
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
        widgets.addSlot(input.get(0), 0, 9).appendTooltip(Text.translatable("klaxon.emi.text.tool_usage.dropped_item"));

        widgets.addSlot(getCatalysts().get(0), 29, 0).appendTooltip(Text.translatable("klaxon.emi.text.tool_usage.tool")).appendTooltip(Text.translatable("klaxon.emi.text.tool_usage.use"));

        widgets.addSlot(output.get(0), 58, 9).recipeContext(this);

        // todo: add dropped item animation here (maybe an accompanying hammer swinging one as well)

        widgets.addText(Text.translatable("klaxon.emi.text.tool_usage.use_compact"), 0, 38, 4210752, false);
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return requiredTool;
    }
}
