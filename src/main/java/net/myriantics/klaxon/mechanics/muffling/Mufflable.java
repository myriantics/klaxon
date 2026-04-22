package net.myriantics.klaxon.mechanics.muffling;

import net.minecraft.world.item.ItemStack;

public interface Mufflable {
    boolean hasMuffler();

    default ItemStack removeMuffler() {
        ItemStack originalMuffler = this.getMuffler();
        this.setMuffler(ItemStack.EMPTY);
        return originalMuffler;
    }

    ItemStack getMuffler();

    void setMuffler(ItemStack stack);
}
