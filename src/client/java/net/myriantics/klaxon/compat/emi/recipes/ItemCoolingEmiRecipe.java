package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.recipe.cooling.ItemCoolingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCoolingEmiRecipe implements EmiRecipe {

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public ItemCoolingEmiRecipe(RecipeEntry<ItemCoolingRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(recipe.value().getInputIngredient()));
        this.output = List.of(EmiStack.of(recipe.value().getOutputStack()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.ITEM_COOLING;
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
        return 92;
    }

    @Override
    public int getDisplayHeight() {
        return 40;
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        widgetHolder.addTooltip(List.of(TooltipComponent.of(Text.translatable("klaxon.emi.text.item_cooling.description").asOrderedText())), 34, 0, 20,  40);
        widgetHolder.addDrawable(34, 12,  20, 20, (drawContext, i, i1, v) -> drawContext.drawItem(Items.WATER_BUCKET.getDefaultStack(), 0, 0));

        widgetHolder.addDrawable(10, 12,  40, 10, (drawContext, i, i1, v) -> EmiTexture.EMPTY_ARROW.render(drawContext, 0, 0, v));
        widgetHolder.addDrawable(50, 12,  40, 10, (drawContext, i, i1, v) -> EmiTexture.EMPTY_ARROW.render(drawContext, 0, 0, v));

        widgetHolder.addSlot(input.get(0), 0, 11);

        widgetHolder.addSlot(output.get(0), 74, 11).recipeContext(this);

    }
}
