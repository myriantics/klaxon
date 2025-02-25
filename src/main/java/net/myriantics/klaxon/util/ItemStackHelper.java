package net.myriantics.klaxon.util;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemStackHelper {
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
