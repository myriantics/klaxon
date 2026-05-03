package net.myriantics.klaxon.mechanics.muffling;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class MufflerStorage implements Container {
    private ItemStack mufflerStack = ItemStack.EMPTY;

    public boolean isPresent() {
        return !this.mufflerStack.isEmpty();
    }

    public void set(ItemStack newMufflerStack) {
        this.mufflerStack = newMufflerStack.copyWithCount(this.getMaxStackSize());
        this.setChanged();
    }

    public ItemStack get() {
        return this.mufflerStack;
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains(KlaxonNBTIds.MUFFLER_STACK)) {
            this.set(ItemStack.parseOptional(registries, tag.getCompound(KlaxonNBTIds.MUFFLER_STACK)));
        }
    }

    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        if (!this.mufflerStack.isEmpty()) {
            tag.put(KlaxonNBTIds.MUFFLER_STACK, this.mufflerStack.save(registries, new CompoundTag()));
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.is(KlaxonItemTags.MUFFLERS);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.get().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.get();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return this.removeItemNoUpdate(slot);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack old = this.get();
        this.set(ItemStack.EMPTY);
        return old;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.set(stack);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.set(ItemStack.EMPTY);
    }
}
