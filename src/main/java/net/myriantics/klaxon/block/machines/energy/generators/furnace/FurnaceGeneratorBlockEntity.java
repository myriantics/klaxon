package net.myriantics.klaxon.block.machines.energy.generators.furnace;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.util.storage.item.ContainerPartition;
import net.myriantics.klaxon.util.storage.item.KlaxonBaseSidedContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class FurnaceGeneratorBlockEntity extends KlaxonBaseSidedContainerBlockEntity {

    protected ContainerPartition fuelPartition;

    protected int remainingFuelDuration = 0;
    protected int initialFuelDuration = 0;

    protected FurnaceGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public FurnaceGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        this(KlaxonBlockEntityTypes.FURNACE_GENERATOR.value(), pos, blockState);
    }

    protected Direction getFacing() {
        return this.getBlockState().getValue(FurnaceGeneratorBlock.FACING);
    }

    protected boolean isLit() {
        return this.remainingFuelDuration > 0;
    }

    @Override
    protected Component getDefaultName() {
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    protected void initPartitions(PartitionBuilder partitions) {
        this.fuelPartition = partitions.partition(1);
    }

    @Override
    protected ContainerPartition getAccessForDirection(@Nullable Direction side) {
        return side == Direction.UP || side == this.getFacing() ? null : this.fuelPartition;
    }

    public void serverTick(ServerLevel level, BlockPos blockPos, BlockState blockState) {
        boolean litAtTickStart = this.isLit();
        boolean editsMade = false;
        if (litAtTickStart) {
            this.consumeFuel();
        }

        ItemStack fuelStack = this.getFuelStack();
        boolean hasFuel = !fuelStack.isEmpty();
        if (this.isLit() || hasFuel) {

            // if we're not lit but have fuel, attempt ignition with that fuel
            if (!this.isLit()) {
                this.remainingFuelDuration = this.getFuelTicksForItem(fuelStack.getItem());
                this.initialFuelDuration = this.remainingFuelDuration;
                // if we successfully lit via the fuel, consume it.
                if (this.isLit()) {
                    editsMade = true;
                    if (hasFuel) {
                        Item fuelItem = fuelStack.getItem();
                        @Nullable Item fuelRemainderItem = fuelItem.getCraftingRemainingItem();
                        fuelStack.shrink(1);
                        if (fuelStack.isEmpty()) {
                            this.setFuelStack(fuelRemainderItem == null ? ItemStack.EMPTY : new ItemStack(fuelRemainderItem));
                        } else if (fuelRemainderItem != null) {
                            this.ejectOverflowRemainder(level, new ItemStack(fuelRemainderItem));
                        }
                    }
                }
            }

            // affect turbine generator
            if (this.isLit()) {

            }
        }

        // if we need to update lit state, do so
        if (litAtTickStart != this.isLit()) {
            editsMade = true;
            blockState = blockState.setValue(FurnaceGeneratorBlock.LIT, this.isLit());
            level.setBlockAndUpdate(blockPos, blockState);
        }

        // if edits were made, save to nbt and alert comparators
        if (editsMade) {
            this.setChanged();
        }
    }

    protected void consumeFuel() {
        this.remainingFuelDuration--;
    }

    protected void ejectOverflowRemainder(ServerLevel level, ItemStack remainder) {
        if (remainder.isEmpty()) {
            return;
        }

        Direction facing = this.getFacing();

        // try inserting into storage in front of generator first
        Storage<ItemVariant> targetStorage = ItemStorage.SIDED.find(level, this.worldPosition.relative(facing), facing.getOpposite());
        if (targetStorage != null) {
            try (Transaction tx = Transaction.openOuter()) {
                ItemVariant remainderVariant = ItemVariant.of(remainder);
                long inserted = targetStorage.insert(remainderVariant, remainder.getCount(), tx);
                if (inserted > 0) {
                    tx.commit();
                    return;
                } else {
                    tx.abort();
                }
            }
        }

        // dump in world if that fails
        Vec3 outputPosition = this.worldPosition.getCenter().relative(facing, 0.7);
        DefaultDispenseItemBehavior.spawnItem(level, remainder, 6, facing, outputPosition);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return super.canPlaceItem(slot, stack) && this.isValidFuel(stack);
    }

    protected ItemStack getFuelStack() {
        return this.fuelPartition.getFirstNonEmptyStack();
    }

    protected void setFuelStack(ItemStack stack) {
        this.fuelPartition.setItem(0, stack);
    }

    public boolean isValidFuel(ItemStack stack) {
        return this.getFuelTicksForItem(stack.getItem()) > 0;
    }

    protected int getFuelTicksForItem(Item item) {
        if (AbstractFurnaceBlockEntity.isNeverAFurnaceFuel(item)) {
            return 0;
        }
        return AbstractFurnaceBlockEntity.getFuel().getOrDefault(item, 0);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.initialFuelDuration = this.getFuelTicksForItem(this.getFuelStack().getItem());
        this.remainingFuelDuration = Math.clamp(tag.getInt(KlaxonNBTIds.REMAINING_FUEL_DURATION), 0, this.initialFuelDuration);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(KlaxonNBTIds.REMAINING_FUEL_DURATION, this.remainingFuelDuration);
    }
}
