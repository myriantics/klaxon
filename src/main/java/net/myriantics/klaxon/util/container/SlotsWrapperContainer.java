package net.myriantics.klaxon.util.container;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SlotsWrapperContainer implements Container {
    private final Container container;
    private final int[] slots;
    private final InventoryStorage storage;

    public static final SlotsWrapperContainer EMPTY = new SlotsWrapperContainer(new SimpleContainer(0));

    public SlotsWrapperContainer(Container container, int... slots) {
        this.container = container;
        this.slots = slots;
        this.storage = InventoryStorage.of(this, null);
    }

    public static SlotsWrapperContainer fullAccess(Container container) {
        int[] fullAccess = new int[container.getContainerSize()];
        for (int i = 0; i < fullAccess.length; i++) {
            fullAccess[i] = i;
        }
        return new SlotsWrapperContainer(container, fullAccess);
    }

    public InventoryStorage getStorage() {
        return this.storage;
    }

    public int[] getSlots() {
        return this.slots;
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
