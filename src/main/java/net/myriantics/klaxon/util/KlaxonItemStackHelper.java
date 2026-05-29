package net.myriantics.klaxon.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class KlaxonItemStackHelper {
    public static void insertAndMergeAndAdd(List<ItemStack> list, ItemStack insertedStack) {
        insertAndMerge(list, insertedStack);

        // insert whatever's left into the list
        if (!insertedStack.isEmpty()) {
            list.add(insertedStack);
        }
    }

    public static void insertAndMerge(List<ItemStack> list, ItemStack insertedStack) {
        for (ItemStack listStack : list) {
            if (listStack.isStackable() && ItemStack.isSameItemSameComponents(listStack, insertedStack)) {
                int listStackSpace = listStack.getMaxStackSize() - listStack.getCount();
                int transferredCount = Math.min(listStackSpace, insertedStack.getCount());
                listStack.grow(transferredCount);
                insertedStack.shrink(transferredCount);
            }

            // no need to check more slots if inserted stack is empty
            if (insertedStack.isEmpty()) {
                break;
            }
        }
    }

    public static ItemStack combineStacksIfPossible(ItemStack stackA, ItemStack stackB) {
        if (stackA.isEmpty()) {
            throw new AssertionError("Cannot merge into an empty stack! Tried to merge " + stackA + " into " + stackB);
        }

        if (canStacksMerge(stackA, stackB)) {
            int maxAcceptedItems = stackA.getMaxStackSize() - stackA.getCount();
            int transferredItems = Math.min(stackB.getCount(), maxAcceptedItems);
            stackA.grow(transferredItems);
            stackB.shrink(transferredItems);
        }

        return stackA;
    }

    public static boolean canStacksMerge(ItemStack stackA, ItemStack stackB) {
        return stackA.getCount() <= stackA.getMaxStackSize() && (stackA.isEmpty() || stackB.isEmpty() || ItemStack.isSameItemSameComponents(stackA, stackB));
    }

    public static boolean hasStackedToMax(ItemStack stack) {
        return stack.getMaxStackSize() == stack.getCount();
    }

    public static int remainingStackCapacity(ItemStack stack) {
        int maxStackSize = stack.getMaxStackSize();
        int count = stack.getCount();
        return count < maxStackSize ? maxStackSize - count : 0;
    }
}
