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
import net.myriantics.klaxon.registry.KlaxonItems;
import net.myriantics.klaxon.recipe.hammering.HammeringRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HammeringEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public HammeringEmiRecipe(RecipeEntry<HammeringRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(recipe.value().getIngredient()));
        this.output = List.of(EmiStack.of(recipe.value().getResult(null)));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.HAMMERING;
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
        widgets.addSlot(input.get(0), 0, 9).appendTooltip(Text.translatable("klaxon.emi.text.hammering.dropped_item"));

        widgets.addSlot(getCatalysts().get(0), 29, 0).appendTooltip(Text.translatable("klaxon.emi.text.hammering.hammer")).appendTooltip(Text.translatable("klaxon.emi.text.hammering.sneak_use"));

        widgets.addSlot(output.get(0), 58, 9).recipeContext(this);

        // todo: add dropped item animation here (maybe an accompanying hammer swinging one as well)

        widgets.addText(Text.translatable("klaxon.emi.text.hammering.sneak_use_compact"), 0, 38, 4210752, false);
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return List.of(EmiStack.of(KlaxonItems.STEEL_HAMMER));
    }
}
