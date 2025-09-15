package net.myriantics.klaxon.util;

import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class KlaxonItemStackHelper {
    public static void insertAndMerge(List<ItemStack> list, ItemStack insertedStack) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack listStack = list.get(i);
            if (listStack.isStackable() && ItemStack.areItemsAndComponentsEqual(listStack, insertedStack)) {
                int listStackSpace = listStack.getMaxCount() - listStack.getCount();
                int transferredCount = Math.min(listStackSpace, insertedStack.getCount());
                listStack.increment(transferredCount);
                insertedStack.decrement(transferredCount);
            }

            // no need to check more slots if inserted stack is empty
            if (insertedStack.isEmpty()) {
                break;
            }
        }

        // insert whatever's left into the list
        if (!insertedStack.isEmpty()) {
            list.add(insertedStack);
        }
    }

    public static ItemStack combineStacksIfPossible(ItemStack stackA, ItemStack stackB) {
        if (canStacksMerge(stackA, stackB)) {
            int maxAcceptedItems = stackA.getMaxCount() - stackA.getCount();
            int transferredItems = Math.min(stackB.getCount(), maxAcceptedItems);
            stackA.increment(transferredItems);
            stackB.decrement(transferredItems);
        }

        return stackA;
    }

    public static boolean canStacksMerge(ItemStack stackA, ItemStack stackB) {
        return stackA.getCount() <= stackA.getMaxCount() && ItemStack.areItemsAndComponentsEqual(stackA, stackB);
    }

    public static boolean hasStackedToMax(ItemStack stack) {
        return stack.getMaxCount() == stack.getCount();
    }
}
