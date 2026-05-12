package net.myriantics.klaxon.recipe.explosive_catalyst;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeSerializers;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;

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
    public boolean matches(ExplosiveCatalystDefinitionRecipeInput input, Level world) {
        return ingredient.test(input.catalystStack());
    }

    @Override
    public ItemStack assemble(ExplosiveCatalystDefinitionRecipeInput input, HolderLookup.Provider lookup) {
        return input.catalystStack();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
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
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.TNT);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeSerializers.EXPLOSIVE_CATALYST_DEFINITION_RECIPE_SERIALIZER.value();
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION;
    }
}
