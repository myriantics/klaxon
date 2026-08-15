package net.myriantics.klaxon.util.storage.item;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ContainerPartition implements Container {
    private final Container container;
    final int[] slots;
    private final int maxStackSize;
    private final InventoryStorage storage;

    public static final ContainerPartition EMPTY = new ContainerPartition(new SimpleContainer(0));

    ContainerPartition(Container container, int... slots) {
        this(container, container.getMaxStackSize(), slots);
    }

    ContainerPartition(Container container, int maxStackSize, int firstSlot, int nextOpenSlot) {
        int size = nextOpenSlot - firstSlot;
        int[] slots = new int[size];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = firstSlot + i;
        }

        this.container = container;
        this.slots = slots;
        this.maxStackSize = maxStackSize;
        this.storage = InventoryStorage.of(this, null);
    }

    ContainerPartition(Container container, int maxStackSize, int... slots) {
        this.container = container;
        this.slots = slots;
        this.maxStackSize = maxStackSize;
        this.storage = InventoryStorage.of(this, null);
    }

    public InventoryStorage getStorage() {
        return this.storage;
    }

    public int[] getSlots() {
        return this.slots;
    }

    public float computeFill() {
        float rawTotal = 0;
        for (int slot = 0; slot < this.getContainerSize(); slot++) {
            rawTotal += this.computeSlotFill(slot);
        }
        return rawTotal / this.getContainerSize();
    }

    public float computeSlotFill(int slot) {
        if (slot >= this.getContainerSize()) {
            return 0f;
        } else {
            ItemStack stack = this.container.getItem(this.slots[slot]);
            if (stack.isEmpty()) {
                return 0;
            }
            int stackLimit = stack.getMaxStackSize();
            int slotLimit = this.getMaxStackSize();
            return (float) stack.getCount() / Math.min(stackLimit, slotLimit);
        }
    }

    @Override
    public int getContainerSize() {
        return this.slots.length;
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return this.container.getMaxStackSize(stack);
    }

    @Override
    public boolean isEmpty() {
        return this.getFirstNonEmptyStack().isEmpty();
    }

    public ItemStack getFirstNonEmptyStack() {
        for (int slot : this.slots) {
            ItemStack stack = this.container.getItem(slot);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.container.getItem(this.slots[slot]);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return this.container.removeItem(this.slots[slot], amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.container.removeItemNoUpdate(this.slots[slot]);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.container.setItem(this.slots[slot], stack);
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

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return this.container.canPlaceItem(this.slots[slot], stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return this.container.canTakeItem(target, this.slots[slot], stack);
    }
}
