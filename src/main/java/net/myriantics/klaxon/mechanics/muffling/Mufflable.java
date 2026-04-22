package net.myriantics.klaxon.mechanics.muffling;

import net.minecraft.world.item.ItemStack;

public interface Mufflable {
    boolean isMuffled();

    ItemStack removeMuffler();

    ItemStack getMuffler();

    void addMuffler(ItemStack stack);
}
