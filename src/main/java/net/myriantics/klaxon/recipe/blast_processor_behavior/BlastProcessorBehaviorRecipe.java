package net.myriantics.klaxon.recipe.blast_processor_behavior;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

public class BlastProcessorBehaviorRecipe implements Recipe<ExplosiveCatalystRecipeInput> {

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
    public boolean matches(ExplosiveCatalystRecipeInput input, World world) {
        return ingredient.test(input.catalystStack());
    }

    @Override
    public ItemStack craft(ExplosiveCatalystRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.BLAST_PROCESSOR_BEHAVIOR_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.BLAST_PROCESSOR_BEHAVIOR;
    }
}
