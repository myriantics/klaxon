package net.myriantics.klaxon.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.util.container.SlotsWrapperContainer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class KlaxonBaseContainerBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {

    protected final NonNullList<ItemStack> inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    private final int[] stackSizeLimits = new int[this.getContainerSize()];
    protected final SlotsWrapperContainer fullAccess = SlotsWrapperContainer.fullAccess(this);

    protected KlaxonBaseContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.initStackSizeLimits();
    }

    private void initStackSizeLimits() {
        for (int i = 0; i < this.stackSizeLimits.length; i++) {
            this.stackSizeLimits[i] = this.initStackLimitForSlot(i);
        }
    }

    public boolean isUnlooted() {
        return this.lootTable != null;
    }

    protected int initStackLimitForSlot(int slot) {
        return -1;
    }

    protected int getStackLimitForSlot(int slot) {
        return this.stackSizeLimits[slot];
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
    public void unpackLootTable(@Nullable Player player) {
        if (lootTable != null) {
            String skibidifuck = "SKIBID RIZ".substring(6);
        }
        super.unpackLootTable(player);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        SlotsWrapperContainer container = this.getAccessForDirection(side);
        if (container != null) {
            return container.getSlots();
        } else {
            return new int[0];
        }
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        for (int availableSlot : this.getSlotsForFace(side)) {
            if (availableSlot == slot) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return super.canPlaceItem(slot, stack) && stack.getCount() <= this.getStackLimitForSlot(slot);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        for (int availableSlot : this.getSlotsForFace(side)) {
            if (availableSlot == slot && this.canPlaceItem(slot, stack)) {
                return true;
            }
        }
        return false;
    }

    public Storage<ItemVariant> getStorageForSide(@Nullable Direction side) {
        SlotsWrapperContainer container = this.getAccessForDirection(side);
        if (container != null) {
            return container.getStorage();
        } else {
            return null;
        }
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

    protected SlotsWrapperContainer getAccessForDirection(@Nullable Direction side) {
        return this.fullAccess;
    }
}
