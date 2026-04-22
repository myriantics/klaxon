package net.myriantics.klaxon.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SlotsWrapperContainer implements Container {
    private final Container container;
    private final int[] slots;

    public SlotsWrapperContainer(Container container, int... slots) {
        this.container = container;
        this.slots = slots;
    }

    @Override
    public int getContainerSize() {
        return this.slots.length;
    }

    @Override
    public boolean isEmpty() {
        for (int slot : this.slots) {
            if (!this.container.getItem(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        for (int availableSlot : this.slots) {
            if (availableSlot == slot) {
                return this.container.getItem(slot);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (this.slotValid(slot)) {
            return this.container.removeItem(slot, amount);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (this.slotValid(slot)) {
            return this.container.removeItemNoUpdate(slot);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (this.slotValid(slot)) {
            this.container.setItem(slot, stack);
        }
    }

    @Override
    public void setChanged() {
        this.container.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void clearContent() {
        for (int slot : this.slots) {
            this.container.setItem(slot, ItemStack.EMPTY);
        }
    }

    private boolean slotValid(int otherSlot) {
        for (int slot : this.slots) {
            if (slot == otherSlot) {
                return true;
            }
        }
        return false;
    }
}
