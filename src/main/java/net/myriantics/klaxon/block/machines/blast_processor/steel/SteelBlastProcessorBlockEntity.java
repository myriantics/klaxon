package net.myriantics.klaxon.block.machines.blast_processor.steel;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlockEntity;
import net.myriantics.klaxon.mechanics.muffling.Mufflable;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

public class SteelBlastProcessorBlockEntity extends AbstractBlastProcessorBlockEntity implements Mufflable {

    private static final int MUFFLER_INDEX = 2;

    protected SteelBlastProcessorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public SteelBlastProcessorBlockEntity(BlockPos pos, BlockState blockState) {
        super(KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR.value(), pos, blockState);
    }

    @Override
    protected int getMaxCatalystStackSize() {
        return 1;
    }

    @Override
    protected int getMaxIngredientStackSize() {
        return 4;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    public Storage<ItemVariant> storageProvider(@Nullable Direction direction) {
        Direction facing = this.getBlockState().getValue(SteelBlastProcessorBlock.HORIZONTAL_FACING);
        if (direction == facing.getOpposite() || direction == Direction.DOWN) { // if back or down do catalyst
            return this.catalystStorage;
        } else if (direction != facing) {
            return this.ingredientStorage;
        } else {
            return null;
        }
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean isMuffled() {
        return this.getMuffler().isEmpty();
    }

    @Override
    public ItemStack removeMuffler() {
        return null;
    }

    @Override
    public ItemStack getMuffler() {
        return this.getItem(MUFFLER_INDEX);
    }

    @Override
    public void addMuffler(ItemStack stack) {
        if (this.getMuffler().isEmpty()) {
            this.setItem(MUFFLER_INDEX, stack);
        }
    }
}
