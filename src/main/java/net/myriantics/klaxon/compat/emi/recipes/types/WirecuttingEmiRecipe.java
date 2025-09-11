package net.myriantics.klaxon.compat.emi.recipes.types;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.recipe.RecipeEntry;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
import net.myriantics.klaxon.compat.emi.recipes.AbstractToolUsageEmiRecipe;
import net.myriantics.klaxon.recipe.tool_usage.AbstractToolUsageRecipe;

public class WirecuttingEmiRecipe extends AbstractToolUsageEmiRecipe {
    public WirecuttingEmiRecipe(RecipeEntry<AbstractToolUsageRecipe> recipe) {
        super(recipe);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiRecipeCategories.WIRECUTTING;
    }
}
