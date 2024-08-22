package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.block.Block;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.recipes.hammer.HammerRecipe;
import net.myriantics.klaxon.util.KlaxonTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class HammeringEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiIngredient> interactionBase;
    private final List<EmiStack> output;

    public HammeringEmiRecipe(HammerRecipe recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getInputA()));
        this.output = List.of(EmiStack.of(recipe.getOutput(null)));
        this.interactionBase = List.of(EmiIngredient.of(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT));
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
        return 36;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(input.get(0), 0, 9);

        widgets.addSlot(interactionBase.get(0), 29, 18);

        widgets.addSlot(getCatalysts().get(0), 29, 0);

        widgets.addSlot(output.get(0), 58, 9).recipeContext(this);
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return List.of(EmiStack.of(KlaxonItems.HAMMER));
    }
}
