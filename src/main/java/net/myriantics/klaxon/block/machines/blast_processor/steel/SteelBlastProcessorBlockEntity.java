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
import net.myriantics.klaxon.util.container.SlotsWrapperContainer;
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
    protected int initStackLimitForSlot(int slot) {
        return switch (slot) {
            case INGREDIENT_INDEX -> 4;
            case CATALYST_INDEX, MUFFLER_INDEX -> 1;
            default -> -1;
        };
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    protected SlotsWrapperContainer getAccessForDirection(@Nullable Direction side) {
        Direction facing = this.getBlockState().getValue(SteelBlastProcessorBlock.HORIZONTAL_FACING);
        if (side == facing.getOpposite() || side == Direction.UP) { // if back or down do catalyst
            return this.catalystContainer;
        } else if (side != facing) {
            return this.ingredientContainer;
        } else {
            return SlotsWrapperContainer.EMPTY;
        }
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean hasMuffler() {
        return !this.getMuffler().isEmpty();
    }

    @Override
    public ItemStack getMuffler() {
        return this.getItem(MUFFLER_INDEX);
    }

    @Override
    public void setMuffler(ItemStack stack) {
        this.setItem(MUFFLER_INDEX, stack);
    }
}
