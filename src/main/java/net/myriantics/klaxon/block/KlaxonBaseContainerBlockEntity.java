package net.myriantics.klaxon.block;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonBaseContainerBlockEntity extends RandomizableContainerBlockEntity {

    protected final NonNullList<ItemStack> inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    private final int[] stackSizeLimits = new int[this.getContainerSize()];
    private int minStackSizeLimit = -1;
    protected final Storage<ItemVariant> fullAccess = InventoryStorage.of(this, null);

    protected KlaxonBaseContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.initStackSizeLimits();
    }

    private void initStackSizeLimits() {
        for (int i = 0; i < this.stackSizeLimits.length; i++) {
            this.stackSizeLimits[i] = this.initStackLimitForSlot(i);
            this.tryUpdateMinStackSizeLimit(this.stackSizeLimits[i]);
        }
    }

    private void tryUpdateMinStackSizeLimit(int potentiallyLower) {
        if (potentiallyLower != -1) {
            if (this.minStackSizeLimit == -1) {
                this.minStackSizeLimit = potentiallyLower;
            } else {
                this.minStackSizeLimit = Math.min(this.minStackSizeLimit, potentiallyLower);
            }
        }
    }

    protected int initStackLimitForSlot(int slot) {
        return -1;
    }

    protected int getStackLimitForSlot(int slot) {
        return this.stackSizeLimits[slot];
    }

    @Override
    public int getMaxStackSize() {
        return this.minStackSizeLimit == -1 ? super.getMaxStackSize() : this.minStackSizeLimit;
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

    public float computeSlotFill(int slot) {
        if (slot >= this.getContainerSize()) {
            return 0f;
        } else {
            ItemStack stack = this.getItem(slot);
            if (stack.isEmpty()) {
                return 0;
            }
            int stackLimit = stack.getMaxStackSize();
            int slotLimit = this.getStackLimitForSlot(slot);
            return (float) stack.getCount() / (slotLimit == -1 ? stackLimit : Math.min(stackLimit, slotLimit));
        }
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

    public Storage<ItemVariant> getStorageForSide(@Nullable Direction direction) {
        return this.fullAccess;
    }
}
