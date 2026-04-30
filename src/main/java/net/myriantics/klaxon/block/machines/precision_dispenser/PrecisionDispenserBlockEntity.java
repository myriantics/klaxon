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
import net.myriantics.klaxon.mechanics.muffling.MufflerStorage;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;

public class PrecisionDispenserBlockEntity extends DispenserBlockEntity {

    private final MufflerStorage mufflerStorage = new MufflerStorage() {
        @Override
        public void onChanged() {
            PrecisionDispenserBlockEntity.this.updateMufflerState();
        }
    };

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
        this.mufflerStorage.save(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.mufflerStorage.load(tag, registries);
    }

    public void setMuffler(ItemStack stack) {
        this.mufflerStorage.set(stack);
        this.setChanged();
    }

    private void updateMufflerState() {
        Level level = this.level;
        if (level != null) {
            BlockPos pos = this.getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (state.hasProperty(PrecisionDispenserBlock.MUFFLED)) {
                level.setBlockAndUpdate(pos, state.setValue(PrecisionDispenserBlock.MUFFLED, this.mufflerStorage.isPresent()));
            }
        }
    }

    public ItemStack getMuffler() {
        return this.mufflerStorage.get();
    }
}
