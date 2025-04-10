package net.myriantics.klaxon.recipe.blast_processor_behavior;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BlastProcessorBehaviorRecipe implements Recipe<RecipeInput> {

    private final Ingredient ingredient;

    private final Identifier behaviorIdentifier;

    public BlastProcessorBehaviorRecipe(Ingredient ingredient, Identifier behaviorIdentifier) {
        this.ingredient = ingredient;
        this.behaviorIdentifier = behaviorIdentifier;
    }

    public Identifier getBehaviorId() {
        return behaviorIdentifier;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public boolean matches(RecipeInput input, World world) {
        return false;
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}
