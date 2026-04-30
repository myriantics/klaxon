package net.myriantics.klaxon.mechanics.muffling;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;

public abstract class MufflerStorage {
    private ItemStack mufflerStack = ItemStack.EMPTY;

    public boolean isPresent() {
        return !this.mufflerStack.isEmpty();
    }

    public void set(ItemStack newMufflerStack) {
        this.mufflerStack = newMufflerStack.copyWithCount(1);
        this.onChanged();
    }

    public ItemStack get() {
        return this.mufflerStack;
    }

    public void onChanged() {
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
}
