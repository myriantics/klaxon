package net.myriantics.klaxon.recipe.tool_usage;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;

public class ToolUsageRecipe implements Recipe<RecipeInput> {
    private final Ingredient requiredTool;
    private final Ingredient inputIngredient;
    private final ItemStack output;

    public ToolUsageRecipe(Ingredient requiredTool, Ingredient inputIngredient, ItemStack output) {
        this.requiredTool = requiredTool;
        this.inputIngredient = inputIngredient;
        this.output = output;
    }

    @Override
    public boolean matches(RecipeInput inventory, World world) {
        return requiredTool.test(inventory.getStackInSlot(0)) && inputIngredient.test(inventory.getStackInSlot(1));
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
        return requiredTool.getMatchingStacks()[0];
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.TOOL_USAGE_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.TOOL_USAGE;
    }

    public Ingredient getRequiredTool() {
        return requiredTool;
    }

    public Ingredient getInputIngredient() {
        return inputIngredient;
    }

    public ItemStack getOutputStack() {
        return output;
    }
}
