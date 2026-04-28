package net.myriantics.klaxon.block.machines.precision_dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;

import java.util.Optional;

public class PrecisionDispenserBlockEntity extends DispenserBlockEntity {

    private ItemStack mufflerStack = ItemStack.EMPTY;

    protected PrecisionDispenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public PrecisionDispenserBlockEntity(BlockPos pos, BlockState blockState) {
        this(KlaxonBlockEntityTypes.PRECISION_DISPENSER.value(), pos, blockState);
    }

    @Override
    public int getRandomSlot(RandomSource random) {
        this.unpackLootTable(null);

        // select in sequence
        for (int i = 0; i < this.getItems().size(); i++) {
            if (!this.getItem(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public float getInaccuracy(float original) {
        return 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.mufflerStack.isEmpty()) {
            tag.put(KlaxonNBTIds.MUFFLER_STACK, this.mufflerStack.save(registries, new CompoundTag()));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(KlaxonNBTIds.MUFFLER_STACK)) {
            this.setMuffler(ItemStack.parseOptional(registries, tag.getCompound(KlaxonNBTIds.MUFFLER_STACK)));
        }
    }

    public void setMuffler(ItemStack stack) {
        this.mufflerStack = stack;
        this.updateMufflerState();
        this.setChanged();
    }

    private void updateMufflerState() {
        Level level = this.level;
        if (level != null) {
            BlockPos pos = this.getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (state.hasProperty(PrecisionDispenserBlock.MUFFLED)) {
                level.setBlockAndUpdate(pos, state.setValue(PrecisionDispenserBlock.MUFFLED, !this.mufflerStack.isEmpty()));
            }
        }
    }

    public ItemStack getMuffler() {
        return this.mufflerStack;
    }
}
