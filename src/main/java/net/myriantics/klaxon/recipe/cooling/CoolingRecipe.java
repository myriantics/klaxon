package net.myriantics.klaxon.recipe.cooling;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;

public class CoolingRecipe implements Recipe<RecipeInput> {
    private final Ingredient inputIngredient;
    private final RegistryEntry<Item> outputItem;

    public CoolingRecipe(Ingredient inputIngredient, RegistryEntry<Item> output) {
        this.inputIngredient = inputIngredient;
        this.outputItem = output;
    }

    @Override
    public boolean matches(RecipeInput input, World world) {
        return inputIngredient.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack inputStack = input.getStackInSlot(0);

        ItemStack outputStack = new ItemStack(outputItem, inputStack.getCount());
        // components persist between the stacks because sure, why not
        // it converts the whole stack at a time so there's no reason to not do this
        outputStack.applyComponentsFrom(inputStack.getComponents());

        return outputStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return new ItemStack(outputItem);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.COOLING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.COOLING;
    }

    public Ingredient getInputIngredient() {
        return inputIngredient;
    }

    public RegistryEntry<Item> getOutputItem() {
        return outputItem;
    }
}
