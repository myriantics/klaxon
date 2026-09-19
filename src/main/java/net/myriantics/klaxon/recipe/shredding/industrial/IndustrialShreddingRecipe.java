package net.myriantics.klaxon.recipe.shredding.industrial;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.myriantics.klaxon.recipe.CompoundOutputRecipe;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;

public interface IndustrialShreddingRecipe extends CompoundOutputRecipe<IndustrialShreddingRecipeInput> {

    Ingredient getIngredient();

    int getTotalShreddingTime();

    @Override
    default RecipeType<?> getType() {
        return KlaxonRecipeTypes.INDUSTRIAL_SHREDDING.value();
    }
}
