package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.util.KlaxonContainerUtil;
import net.myriantics.klaxon.util.storage.item.ContainerPartition;
import net.myriantics.klaxon.util.storage.item.KlaxonBaseContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class BaseIndustrialShredderBlockEntity extends KlaxonBaseContainerBlockEntity {

    protected BaseIndustrialShredderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        try {
            return new IndustrialShredderMenu(
                    id,
                    inventory,
                    KlaxonContainerUtil.concatenate(this.getTop().shreddingInputPartition, this.getBottom().outputStorage),
                    this.getTop().dataAccess,
                    ContainerLevelAccess.create(this.level, this.worldPosition)
            );
        } catch (IllegalStateException e) {
            return null;
        }
    }

    protected abstract IndustrialShredderTopBlockEntity getTop() throws IllegalStateException;

    protected abstract IndustrialShredderBottomBlockEntity getBottom() throws IllegalStateException;

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().asItem().getDescriptionId());
    }

    protected abstract ContainerPartition getAutomationAccessiblePartition();

    protected abstract @Nullable BaseIndustrialShredderBlockEntity getCounterpart();

}
