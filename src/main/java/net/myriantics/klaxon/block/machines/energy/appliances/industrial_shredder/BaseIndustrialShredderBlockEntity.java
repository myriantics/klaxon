package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorBlockEntity;
import net.myriantics.klaxon.util.KlaxonContainerUtil;
import net.myriantics.klaxon.util.storage.item.KlaxonBaseContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class BaseIndustrialShredderBlockEntity extends KlaxonBaseContainerBlockEntity {

    protected static final ContainerData EMPTY = new ContainerData() {
        @Override
        public int get(int index) {
            return 0;
        }

        @Override
        public void set(int index, int value) {

        }

        @Override
        public int getCount() {
            return 0;
        }
    };

    protected BaseIndustrialShredderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        @Nullable BaseIndustrialShredderBlockEntity counterpart = this.getCounterpart();
        if (counterpart == null) {
            return null;
        }

        return new IndustrialShredderMenu(
                id,
                inventory,
                this instanceof IndustrialShredderTopBlockEntity top
                        ? KlaxonContainerUtil.concatenate(top.shreddingInput, ((IndustrialShredderBottomBlockEntity) counterpart).output)
                        : KlaxonContainerUtil.concatenate(((IndustrialShredderTopBlockEntity) counterpart).shreddingInput, ((IndustrialShredderBottomBlockEntity) this).output),
                ContainerLevelAccess.create(level, this.worldPosition)
        );
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().asItem().getDescriptionId());
    }

    protected abstract @Nullable BaseIndustrialShredderBlockEntity getCounterpart();

}
