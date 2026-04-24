package net.myriantics.klaxon.mechanics.muffling;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface MufflableBlock {
    boolean hasMuffler(Level level, BlockPos pos);

    default ItemStack removeMuffler(Level level, BlockPos pos) {
        ItemStack originalMuffler = this.getMuffler(level, pos);
        this.setMuffler(level, pos, ItemStack.EMPTY);
        return originalMuffler;
    }

    ItemStack getMuffler(Level level, BlockPos pos);

    void setMuffler(Level level, BlockPos pos, ItemStack stack);
}
