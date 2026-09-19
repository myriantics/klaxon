package net.myriantics.klaxon.block.machines.energy.appliances.auto_hammer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.util.storage.energy.KlaxonEnergyStorageProvider;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class AutoHammerTopBlockEntity extends BlockEntity implements KlaxonEnergyStorageProvider {

    protected long storedEnergy;
    protected long energyCapacity;
    protected long maxEnergyInsert;

    public AutoHammerTopBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public @Nullable EnergyStorage getEnergyStorageForSide(@Nullable Direction direction) {
        return null;
    }

    protected EnergyStorage initEnergyStorage() {
        return null;
    }
}
