package net.myriantics.klaxon.recipe.hammering;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;

public class HammeringRecipe implements Recipe<RecipeInput>{
    private final Ingredient inputA;
    private final ItemStack output;

    public HammeringRecipe(Ingredient inputA, ItemStack output) {
        this.inputA = inputA;
        this.output = output;
    }

    @Override
    public boolean matches(RecipeInput inventory, World world) {
        return inputA.test(inventory.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.copy();
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(KlaxonItems.STEEL_HAMMER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.HAMMERING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.HAMMERING;
    }

    public Ingredient getInputIngredient() {
        return inputA;
    }

    public ItemStack getOutputStack() {
        return output;
    }
}
