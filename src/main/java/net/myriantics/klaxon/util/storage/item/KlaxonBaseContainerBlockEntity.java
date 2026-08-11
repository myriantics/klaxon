package net.myriantics.klaxon.util.storage.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.util.storage.KlaxonStorageProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class KlaxonBaseContainerBlockEntity extends RandomizableContainerBlockEntity implements KlaxonStorageProvider<ItemVariant> {

    protected final NonNullList<ItemStack> inventory;
    private final ContainerPartition[] partitionedSlots;
    protected final CombinedSlottedStorage<ItemVariant, ?> fullAccess;

    protected KlaxonBaseContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        PartitionBuilder builder = new PartitionBuilder();
        this.initPartitions(builder);
        this.partitionedSlots = builder.build();
        this.inventory = NonNullList.withSize(this.partitionedSlots.length, ItemStack.EMPTY);
        this.fullAccess = new CombinedSlottedStorage<>(builder.partitions.stream().map(ContainerPartition::getStorage).toList());
    }

    protected abstract void initPartitions(PartitionBuilder partitions);

    public @Nullable ContainerData getContainerData() {
        return null;
    }

    @Override
    public final int getContainerSize() {
        return partitionedSlots.length;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.set(i, items.get(i));
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        stack.limitSize(this.partitionedSlots[slot].getMaxStackSize());
    }

    @Override
    public boolean stillValid(Player player) {
        return super.stillValid(player) && !this.isUnlooted();
    }

    public boolean isUnlooted() {
        return this.lootTable != null;
    }

    @Override
    public boolean canOpen(Player player) {
        return this.canUnlock(player);
    }

    protected boolean canUnlock(Player player) {
        // had to make a whole access widener for the ability to configure the sound
        if (!player.isSpectator() && !this.lockKey.unlocksWith(player.getMainHandItem())) {
            player.displayClientMessage(Component.translatable("container.isLocked", this.getDisplayName()), true);
            player.playNotifySound(this.getLockedSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return super.canPlaceItem(slot, stack) && this.getItem(slot).getCount() < this.partitionedSlots[slot].getMaxStackSize();
    }

    protected SoundEvent getLockedSound() {
        return SoundEvents.CHEST_LOCKED;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.inventory, registries);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.clearContent();
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.inventory, registries);
        }
    }

    public @Nullable Storage<ItemVariant> getStorageForSide(@Nullable Direction direction) {
        return this.fullAccess;
    }

    protected class PartitionBuilder {
        private final ArrayList<ContainerPartition> partitions = new ArrayList<>();
        int currentNextOpenSlot = 0;

        private PartitionBuilder() {
        }

        public ContainerPartition partition(int slotCount) {
            return this.partition(slotCount, 99);
        }

        public ContainerPartition partition(int slotCount, int maxStackSize) {
            if (slotCount <= 0) {
                throw new IllegalArgumentException("Inventory partition must have at least one slot!");
            }
            if (maxStackSize < 0) {
                throw new IllegalArgumentException("Partition max stack size cannot be negative!");
            }
            int nextOpenSlot = this.currentNextOpenSlot + slotCount;
            ContainerPartition partition = new ContainerPartition(KlaxonBaseContainerBlockEntity.this, maxStackSize, this.currentNextOpenSlot, nextOpenSlot);
            this.partitions.add(partition);
            this.currentNextOpenSlot = nextOpenSlot;
            return partition;
        }

        public ContainerPartition[] build() {
            ContainerPartition[] builtPartitions = new ContainerPartition[this.currentNextOpenSlot];
            for (ContainerPartition partition : this.partitions) {
                for (int i = 0; i < partition.slots.length; i++) {
                    builtPartitions[partition.slots[i]] = partition;
                }
            }
            return builtPartitions;
        }
    }
}
