package net.myriantics.klaxon.recipes.hammer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public interface HammerCraftingInventory extends Inventory, RecipeInput {
    @Override
    default int size() {
        return 0;
    }

    @Override
    default ItemStack getStackInSlot(int slot) {
        return null;
    }

    @Override
    default int getSize() {
        return 0;
    }

    @Override
    default boolean isEmpty() {
        return false;
    }

    @Override
    default ItemStack getStack(int slot) {
        return null;
    }

    @Override
    default ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    default ItemStack removeStack(int slot) {
        return null;
    }

    @Override
    default void setStack(int slot, ItemStack stack) {

    }

    @Override
    default void markDirty() {

    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    default void clear() {

    }
}
