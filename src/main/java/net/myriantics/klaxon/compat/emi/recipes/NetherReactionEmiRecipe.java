package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.block.Block;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.api.BlockIngredient;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipe;
import net.myriantics.klaxon.registry.item.KlaxonBlockItems;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NetherReactionEmiRecipe implements EmiRecipe {

    private final Identifier id;
    private final List<EmiIngredient> inputStacks;
    private final List<EmiStack> outputStacks;

    public NetherReactionEmiRecipe(RecipeEntry<NetherReactionRecipe> recipeEntry) {
        this.id = recipeEntry.id();
        this.inputStacks = Collections.singletonList(EmiIngredient.of(Arrays.stream(
                recipeEntry.value().getBlockIngredient().getDisplayStacks()
        ).map(EmiStack::of).toList()));
        this.outputStacks = List.of(EmiStack.of(
                KlaxonBlockItems.getBlockDisplayStack(recipeEntry.value().getOutputBlock())
        ));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.NETHER_REACTION;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputStacks;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputStacks;
    }

    @Override
    public int getDisplayWidth() {
        return 96;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(getInputs().get(0), 0, 0);
        widgets.addSlot(outputStacks.get(0), 52, 0).recipeContext(this);
    }
}
