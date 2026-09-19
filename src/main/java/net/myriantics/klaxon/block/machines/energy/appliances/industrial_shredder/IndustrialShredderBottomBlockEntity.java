package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.util.storage.item.ContainerPartition;
import org.jetbrains.annotations.Nullable;

public class IndustrialShredderBottomBlockEntity extends BaseIndustrialShredderBlockEntity {

    protected IndustrialShredderTopBlockEntity counterpartCache = null;
    protected ContainerPartition outputStorage;

    public IndustrialShredderBottomBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.INDUSTRIAL_SHREDDER_BOTTOM.value(), pos, state);
    }

    protected IndustrialShredderBottomBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected @Nullable BaseIndustrialShredderBlockEntity getCounterpart() {
        if (this.counterpartCache == null) {
            if (this.level != null && this.level.getBlockEntity(this.worldPosition.above()) instanceof IndustrialShredderTopBlockEntity be) {
                return this.counterpartCache = be;
            }
        }

        return this.counterpartCache;
    }

    @Override
    protected ContainerPartition getAutomationAccessiblePartition() {
        return this.outputStorage;
    }

    @Override
    protected void initPartitions(PartitionBuilder partitions) {
        this.outputStorage = partitions.partition(9);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new DispenserMenu(containerId, inventory);
    }
}
