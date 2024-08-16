package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class BlastProcessorCraftingInventory implements Inventory {
    private final BlastProcessorScreenHandler handler;
    private final DefaultedList<ItemStack> stacks;

    public BlastProcessorCraftingInventory(BlastProcessorScreenHandler handler, int size) {
        this(handler, size, DefaultedList.ofSize(size, ItemStack.EMPTY));
    }

    public BlastProcessorCraftingInventory(BlastProcessorScreenHandler handler, int size, Inventory inventory) {
        this.handler = handler;
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(size);
        for (int i = 0; i < inventory.size(); i++) {
            stacks.add(i, inventory.getStack(i));
        }
        this.stacks = stacks;
    }

    public BlastProcessorCraftingInventory(BlastProcessorScreenHandler handler, int size, DefaultedList<ItemStack> stacks) {
        this.handler = handler;
        this.stacks = stacks;
    }

    @Override
    public int size() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.stacks) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot >= this.size() ? ItemStack.EMPTY : this.stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.stacks, slot, amount);
        if (!itemStack.isEmpty()) {
            this.handler.onContentChanged(this);
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.stacks, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
        this.handler.onContentChanged(this);
    }

    @Override
    public void markDirty() {
        this.handler.onContentChanged(this);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }
}
