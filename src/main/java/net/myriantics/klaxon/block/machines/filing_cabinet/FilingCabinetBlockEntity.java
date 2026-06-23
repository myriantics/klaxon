package net.myriantics.klaxon.block.machines.filing_cabinet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.util.container.ContainerPartition;
import net.myriantics.klaxon.util.container.KlaxonBaseContainerBlockEntity;
import net.myriantics.klaxon.util.container.KlaxonBaseSidedContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class FilingCabinetBlockEntity extends KlaxonBaseSidedContainerBlockEntity {

    private ContainerPartition items;

    protected FilingCabinetBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected ContainerPartition getAccessForDirection(@Nullable Direction side) {
        return items;
    }

    public FilingCabinetBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.FILING_CABINET.value(), pos, state);
    }

    @Override
    protected void initPartitions(PartitionBuilder partitions) {
        this.items = partitions.partition(9);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        // if open, update comparators around drawer block too
        if (this.level != null && ((FilingCabinetBaseBlock) this.getBlockState().getBlock()).isOpen(this.level, this.getBlockState(), this.worldPosition)) {
            this.level.updateNeighbourForOutputSignal(((FilingCabinetBaseBlock) this.getBlockState().getBlock()).findDrawerPos(this.getBlockState(), this.worldPosition), ((FilingCabinetBaseBlock) this.getBlockState().getBlock()).getDrawerBlock());
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new DispenserMenu(containerId, inventory, this.items);
    }

    public int getAnalogSignalStrength() {
        return this.items == null ? 0 : AbstractContainerMenu.getRedstoneSignalFromContainer(this.items);
    }
}
