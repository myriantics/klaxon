package net.myriantics.klaxon.util.storage.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonBaseSidedContainerBlockEntity extends KlaxonBaseContainerBlockEntity implements WorldlyContainer {

    protected KlaxonBaseSidedContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        ContainerPartition container = this.getAccessForDirection(side);
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
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        for (int availableSlot : this.getSlotsForFace(side)) {
            if (availableSlot == slot && this.canPlaceItem(slot, stack)) {
                return true;
            }
        }
        return false;
    }

    public @Nullable Storage<ItemVariant> getStorageForSide(@Nullable Direction side) {
        if (side == null) {
            return super.getStorageForSide(null);
        } else {
            ContainerPartition container = this.getAccessForDirection(side);
            if (container != null) {
                return container.getStorage();
            } else {
                return null;
            }
        }
    }

    protected abstract ContainerPartition getAccessForDirection(@Nullable Direction side);
}
