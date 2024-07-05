package net.myriantics.klaxon.block.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.util.KlaxonTags;
import org.jetbrains.annotations.Nullable;

public class BlastChamberBlockEntity extends BlockEntity implements ImplementedInventory, SidedInventory {
    private DefaultedList<ItemStack> inventory;
    protected static final int PROCESS_ITEM_INDEX = 0;
    protected static final int CATALYST_INDEX = 1;
    private static final int[] PROCESS_ITEM_SLOTS = new int[]{PROCESS_ITEM_INDEX};
    private static final int[] CATALYST_ITEM_SLOTS = new int[]{CATALYST_INDEX};
    private static final int MaxItemStackCount = 1;

    public BlastChamberBlockEntity(BlockPos pos, BlockState state) {
        super(KlaxonBlockEntities.BLAST_CHAMBER_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        this.markDirty();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.markDirty();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public int size() {
        return 2;
    }

    public void tick(World world1, BlockPos pos, BlockState state1) {
    }

    @Override
    public int getMaxCountPerStack() {
        return MaxItemStackCount;
    }


    @Override
    public int[] getAvailableSlots(Direction side) {
        if(side == Direction.UP || side == Direction.DOWN) {
            return CATALYST_ITEM_SLOTS;
        }
        return PROCESS_ITEM_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return (slot == PROCESS_ITEM_INDEX || (slot == CATALYST_INDEX && isValidCatalyst(stack)))
                && this.getStack(slot).isEmpty()
                && stack.getCount() == 1;
        // gotta love hacky fixes (maxcountperstack wasnt working)
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot != CATALYST_INDEX;
    }

    // hi are u a fuel
    private boolean isValidCatalyst(ItemStack stack) {
        return stack.isIn(KlaxonTags.Items.BLAST_CHAMBER_FUEL_REGULAR) ||
                stack.isIn(KlaxonTags.Items.BLAST_CHAMBER_FUEL_SUPER) ||
                stack.isIn(KlaxonTags.Items.BLAST_CHAMBER_FUEL_HYPER);
    }
}
