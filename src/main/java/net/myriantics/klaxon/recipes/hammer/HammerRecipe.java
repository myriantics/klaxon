package net.myriantics.klaxon.recipes.hammer;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HammerRecipe implements Recipe<CraftingInventory>{
    private final Ingredient inputA;
    private final ItemStack result;
    private final Identifier id;

    public HammerRecipe(Ingredient inputA, ItemStack result, Identifier id) {
        this.inputA = inputA;
        this.result = result;
        this.id = id;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        return inputA.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    public Ingredient getInputA() {
        return inputA;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.result;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HammerRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<HammerRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "hammer_recipe";
    }
}
