package net.myriantics.klaxon.recipe.hammer;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;

public class HammeringRecipe implements Recipe<RecipeInput>{
    private final Ingredient inputA;
    private final ItemStack result;

    public HammeringRecipe(Ingredient inputA, ItemStack result ) {
        this.inputA = inputA;
        this.result = result;
    }

    @Override
    public boolean matches(RecipeInput inventory, World world) {
        return inputA.test(inventory.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result.copy();
    }

    public Ingredient getIngredient() {
        return inputA;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(KlaxonItems.HAMMER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.HAMMERING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.HAMMERING;
    }
}
