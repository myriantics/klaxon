package net.myriantics.klaxon.recipes.hammer;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.recipes.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorRecipeSerializer;

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
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
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
