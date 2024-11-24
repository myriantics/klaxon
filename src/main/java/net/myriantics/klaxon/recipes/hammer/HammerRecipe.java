package net.myriantics.klaxon.recipes.hammer;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.recipes.KlaxonRecipeTypes;

public class HammerRecipe implements Recipe<SimpleInventory>{
    private final Ingredient inputA;
    private final ItemStack result;
    private final Identifier id;

    public HammerRecipe(Ingredient inputA, ItemStack result, Identifier id) {
        this.inputA = inputA;
        this.result = result;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        return inputA.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory input, RegistryWrapper.WrapperLookup lookup) {
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

    public Ingredient getInputA() {
        return inputA;
    }

    public Identifier getId() {
        return this.id;
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
