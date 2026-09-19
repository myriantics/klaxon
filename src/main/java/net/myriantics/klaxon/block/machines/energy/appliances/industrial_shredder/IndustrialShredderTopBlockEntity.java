package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.util.storage.energy.KlaxonEnergyStorageProvider;
import net.myriantics.klaxon.util.storage.item.ContainerPartition;
import net.myriantics.klaxon.util.storage.item.KlaxonBaseContainerBlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class IndustrialShredderTopBlockEntity extends BaseIndustrialShredderBlockEntity implements KlaxonEnergyStorageProvider {

    private static final AABB SUCK_AABB = Block.box(0, 0, 0, 16, EntityType.ITEM.getHeight() * 16, 16).toAabbs().getFirst();

    protected IndustrialShredderBottomBlockEntity counterpartCache = null;
    protected ContainerPartition shreddingInput;
    protected EnergyStorage energyStorage = new SimpleEnergyStorage(1000, 32, 32);

    public IndustrialShredderTopBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.INDUSTRIAL_SHREDDER_TOP.value(), pos, state);
    }

    protected IndustrialShredderTopBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected @Nullable BaseIndustrialShredderBlockEntity getCounterpart() {
        if (this.counterpartCache == null) {
            if (this.level != null && this.level.getBlockEntity(this.worldPosition.below()) instanceof IndustrialShredderBottomBlockEntity be) {
                return this.counterpartCache = be;
            }
        }

        return this.counterpartCache;
    }

    @Override
    protected void initPartitions(PartitionBuilder partitions) {
        this.shreddingInput = partitions.partition(1);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    public void serverTick(Level level, BlockPos blockPos, BlockState blockState) {

    }

    protected Direction getFacing() {
        return this.getBlockState().getValue(IndustrialShredderTopBlock.FACING);
    }

    @Override
    public @Nullable EnergyStorage getEnergyStorageForSide(@Nullable Direction direction) {
        return direction == this.getFacing().getOpposite() ? this.energyStorage : null;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        // tag.putLong(KlaxonNBTIds.STORED_POWER, this.energyStorage.getAmount());
    }
}
