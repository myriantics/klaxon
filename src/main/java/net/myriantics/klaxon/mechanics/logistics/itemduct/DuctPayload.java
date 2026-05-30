package net.myriantics.klaxon.mechanics.logistics.itemduct;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;

public final class DuctPayload {

    public final NonNullList<ItemStack> stacks;

    public DuctPayload(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public static DuctPayload extract(Storage<ItemVariant> storage, int size) {
        DuctPayload payload = new DuctPayload(size);
        payload.extract(storage);
        return payload;
    }

    public void extract(Storage<ItemVariant> storage) {

        for (StorageView<ItemVariant> view : storage.nonEmptyViews()) {
            ItemVariant variant = view.getResource();
            int variantMaxStackSize = variant.toStack().getMaxStackSize();


            int availableSpaceForVariant = 0;
            for (ItemStack existing : this.stacks) {
                if (existing.isEmpty()) {
                    availableSpaceForVariant += variantMaxStackSize;
                } else if (variant.matches(existing)) {
                    availableSpaceForVariant += KlaxonItemStackHelper.remainingStackCapacity(existing);
                }
            }

            if (availableSpaceForVariant == 0) {
                continue;
            }

            try (Transaction tx = Transaction.openOuter()) {
                long extractedAmount = storage.extract(variant, availableSpaceForVariant, tx);

                for (int i = 0; i < this.stacks.size(); i++) {
                    ItemStack stack = this.stacks.get(i);
                    if (stack.isEmpty()) {
                        ItemStack newStack = variant.toStack(Math.min(variant.getComponentMap().getOrDefault(DataComponents.MAX_STACK_SIZE, 64), Math.toIntExact(extractedAmount)));
                        this.stacks.set(i, newStack);
                        extractedAmount -= newStack.getCount();
                    } else if (variant.matches(stack)) {
                        int remainingCapacity = KlaxonItemStackHelper.remainingStackCapacity(stack);
                        int spentAmount = Math.min(remainingCapacity, Math.toIntExact(extractedAmount));
                        this.stacks.get(i).grow(spentAmount);
                        extractedAmount -= spentAmount;
                    }
                }
                tx.commit();
            }

        }
    }

    /**
     *
     * @param storage The storage to insert into
     * @return Only returns true if payload is fully empty
     */
    public boolean insert(Storage<ItemVariant> storage) {
        boolean fullyEmpty = true;
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                try (Transaction tx = Transaction.openOuter()) {
                    int count = stack.getCount();
                    int inserted = Math.toIntExact(storage.insert(ItemVariant.of(stack), count, tx));

                    if (inserted > 0) {
                        tx.commit();
                        stack.setCount(count - inserted);
                        fullyEmpty &= stack.isEmpty();
                    } else {
                        tx.abort();
                    }
                }
            }
        }
        return fullyEmpty;
    }

}
