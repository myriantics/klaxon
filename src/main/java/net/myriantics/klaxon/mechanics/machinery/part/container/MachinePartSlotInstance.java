package net.myriantics.klaxon.mechanics.machinery.part.container;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.machinery.part.MachinePartUtil;
import net.myriantics.klaxon.mechanics.machinery.part.data.StackingStrategy;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class MachinePartSlotInstance extends SnapshotParticipant<MachinePartSlotInstance.Data> {

    private final MachinePartContainer container;

    private final MachinePartSlotBehavior behavior;
    private final StackingStrategy strategy;

    private ItemStack stack = ItemStack.EMPTY;
    private DataComponentPatch stackedPartComponents = DataComponentPatch.EMPTY;

    private @Nullable Storage<ItemVariant> storage = null;

    public MachinePartSlotInstance(MachinePartSlotBehavior behavior, MachinePartContainer container, StackingStrategy strategy) {
        this.behavior = behavior;
        this.container = container;
        this.strategy = strategy;
    }

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        if (!this.stack.isEmpty()) {
            tag.put(KlaxonNBTIds.STACK, this.stack.save(registries, tag));
        }
        StackingStrategy.CODEC.encode(this.strategy, NbtOps.INSTANCE, tag).ifSuccess(t -> tag.put(KlaxonNBTIds.STACKING_STRATEGY, t));
        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries, @Nullable Level level) {
        ItemStack.parse(registries, tag.getCompound(KlaxonNBTIds.STACK)).ifPresent(stack -> {
            if (level == null) {
                this.set(stack, DataComponentPatch.EMPTY);
            } else {
                this.setRaw(stack);
                this.stackedPartComponents = DataComponentPatch.EMPTY;
            }
        });
    }

    int maxStackSize() {
        return this.behavior.getMaxCount();
    }

    boolean accepts(DataComponentPatch stackedIncomingPartComponents) {
        return this.behavior.validateUpgrades(stackedIncomingPartComponents);
    }

    @NotNull
    public ItemStack getItem() {
        return this.stack;
    }

    private void setRaw(ItemStack stack) {
        this.stack = stack;
    }

    public void set(ItemStack stack) {
        this.set(
                stack,
                MachinePartUtil.getStackedPartComponents(
                        stack,
                        this.container.level(),
                        this.strategy
                )
        );
    }

    public void set(ItemStack stack, DataComponentPatch summedPartComponents) {
        this.setRaw(stack);
        if (stack.getCount() > 1) {
            this.stackedPartComponents = MachinePartUtil.stackForCount(summedPartComponents, stack.getCount(), StackingStrategy.ADD);
        } else {
            this.stackedPartComponents = summedPartComponents;
        }
        this.container.setChanged();
    }

    boolean isEmpty() {
        return this.stack.isEmpty();
    }

    public @NotNull Storage<ItemVariant> storage() {
        if (this.storage == null) {
            return this.initStorage();
        } else {
            return this.storage;
        }
    }

    private Storage<ItemVariant> initStorage() {
        return this.storage = new StorageImpl();
    }

    @Override
    protected Data createSnapshot() {
        return new Data(this.stack.copy(), this.stackedPartComponents);
    }

    @Override
    protected void readSnapshot(Data snapshot) {
        this.stack = snapshot.stack;
        this.stackedPartComponents = snapshot.stackedPartComponents;
    }

    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        this.container.setChanged();
    }

    public void onLevelPopulated(Level level) {
        this.stackedPartComponents = MachinePartUtil.stackForCount(
                MachinePartUtil.getStackedPartComponents(
                        this.stack,
                        level,
                        this.strategy
                ),
                this.stack.getCount(),
                this.strategy
        );
    }

    protected record Data(ItemStack stack, DataComponentPatch stackedPartComponents) {
    }

    protected class StorageImpl implements Storage<ItemVariant> {
        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            if (maxAmount < 1) {
                return 0;
            }
            if (!MachinePartSlotInstance.this.isEmpty()) {
                return 0;
            }


            DataComponentPatch summedPartComponents;
            if (MachinePartSlotInstance.this.container.level() == null) {
                summedPartComponents = DataComponentPatch.EMPTY;
            } else {
                summedPartComponents = MachinePartUtil.stack(
                        resource.getComponents(),
                        Objects.requireNonNullElse(MachinePartUtil.getDynamicPartComponents(resource.getItem(), MachinePartSlotInstance.this.container.level()), DataComponentPatch.EMPTY),
                        MachinePartSlotInstance.this.strategy
                );
            }

            if (!MachinePartSlotInstance.this.accepts(summedPartComponents)) {
                return 0;
            }

            int inserted = Math.min(MachinePartSlotInstance.this.maxStackSize(), KlaxonMathHelper.trimLong(maxAmount));

            MachinePartSlotInstance.this.updateSnapshots(transaction);
            MachinePartSlotInstance.this.set(resource.toStack(inserted), summedPartComponents);

            return inserted;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);

            if (maxAmount < 1) {
                return 0;
            }
            if (MachinePartSlotInstance.this.isEmpty()) {
                return 0;
            }
            if (!resource.matches(MachinePartSlotInstance.this.stack)) {
                return 0;
            }

            int count = MachinePartSlotInstance.this.stack.getCount();
            int extracted = Math.min(count, KlaxonMathHelper.trimLong(maxAmount));

            MachinePartSlotInstance.this.updateSnapshots(transaction);
            if (extracted < count) {
                MachinePartSlotInstance.this.stack.shrink(extracted);
            } else {
                MachinePartSlotInstance.this.set(ItemStack.EMPTY, DataComponentPatch.EMPTY);
            }

            return extracted;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator<StorageView<ItemVariant>> iterator() {
            return new Iterator<>() {
                private boolean hasNext = true;

                @Override
                public boolean hasNext() {
                    return this.hasNext;
                }

                @Override
                public StorageView<ItemVariant> next() {
                    if (!this.hasNext) {
                        throw new NoSuchElementException();
                    }

                    this.hasNext = false;
                    return (StorageView<ItemVariant>) StorageImpl.this;
                }
            };
        }
    }
}
