package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

public class ExplosiveCatalystDefinitionRecipe implements Recipe<ExplosiveCatalystDefinitionRecipeInput> {
    private final Ingredient ingredient;
    private final ExplosiveCatalystData data;
    private final boolean isHidden;

    public ExplosiveCatalystDefinitionRecipe(Ingredient input, ExplosiveCatalystData data, boolean isHidden) {
        this.ingredient = input;
        this.data = data;
        this.isHidden = isHidden;
    }

    // to whom it may concern: CHECK WHAT INDEX YOU'RE TRYING TO PULL FROM
    // GAH
    @Override
    public boolean matches(ExplosiveCatalystDefinitionRecipeInput input, World world) {
        return ingredient.test(input.catalystStack());
    }

    @Override
    public ItemStack craft(ExplosiveCatalystDefinitionRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return input.catalystStack();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ExplosiveCatalystData getData() {
        return this.data;
    }

    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.TNT);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION;
    }
}
