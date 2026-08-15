package net.myriantics.klaxon.block.machines.precision_dispenser;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.muffling.MufflerStorage;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.util.storage.KlaxonStorageProvider;
import org.jetbrains.annotations.Nullable;

public class PrecisionDispenserBlockEntity extends DispenserBlockEntity implements KlaxonStorageProvider<ItemVariant> {

    final MufflerStorage mufflerStorage = new MufflerStorage() {
        @Override
        public void setChanged() {
            PrecisionDispenserBlockEntity.this.updateMufflerState();
        }
    };
    final Storage<ItemVariant> storage = InventoryStorage.of(this, null);

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

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new PrecisionDispenserMenu(containerId, inventory, this, ContainerLevelAccess.create(this.level, this.getBlockPos()));
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

    @Override
    public @Nullable Storage<ItemVariant> getStorageForSide(@Nullable Direction direction) {
        return this.storage;
    }
}
