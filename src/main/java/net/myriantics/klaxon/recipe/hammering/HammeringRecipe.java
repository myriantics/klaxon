package net.myriantics.klaxon.recipe.hammering;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.KlaxonItems;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;

public class HammeringRecipe implements Recipe<Inventory>{

    private final Ingredient inputA;
    private final ItemStack result;

    private final Identifier identifier;

    public HammeringRecipe(Identifier id, Ingredient inputA, ItemStack result ) {
        this.inputA = inputA;
        this.result = result;
        this.identifier = id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return inputA.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory input, DynamicRegistryManager registryManager) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.result.copy();
    }

    public Ingredient getIngredient() {
        return inputA;
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

    @Override
    public Identifier getId() {
        return identifier;
    }
}
