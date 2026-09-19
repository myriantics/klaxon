package net.myriantics.klaxon.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public abstract class KlaxonContainerUtil {
    public static Container concatenate(Container... containers) {
        int totalSize = Arrays.stream(containers).mapToInt(Container::getContainerSize).sum();

        return new Container() {

            @Override
            public int getContainerSize() {
                return totalSize;
            }

            @Override
            public boolean isEmpty() {
                for (Container container : containers) {
                    if (!container.isEmpty()) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public ItemStack getItem(int slot) {
                int currentSlot = 0;
                for (Container container : containers) {
                    if (slot < currentSlot + container.getContainerSize()) {
                        return container.getItem(slot - currentSlot);
                    }
                }
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeItem(int slot, int amount) {
                int currentSlot = 0;
                for (Container container : containers) {
                    if (slot < currentSlot + container.getContainerSize()) {
                        return container.removeItem(slot - currentSlot, amount);
                    }
                }
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                int currentSlot = 0;
                for (Container container : containers) {
                    if (slot < currentSlot + container.getContainerSize()) {
                        return container.removeItemNoUpdate(slot - currentSlot);
                    }
                }
                return ItemStack.EMPTY;
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                int currentSlot = 0;
                for (Container container : containers) {
                    if (slot < currentSlot + container.getContainerSize()) {
                        container.setItem(slot - currentSlot, stack);
                        return;
                    }
                }
            }

            @Override
            public void setChanged() {
                for (Container container : containers) {
                    container.setChanged();
                }
            }

            @Override
            public boolean stillValid(Player player) {
                for (Container container : containers) {
                    if (container.stillValid(player)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void clearContent() {
                for (Container container : containers) {
                    container.clearContent();
                }
            }
        };
    }
}
