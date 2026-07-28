package net.myriantics.klaxon.util.storage.energy;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public interface KlaxonEnergyStorageProvider {
    @Nullable EnergyStorage getStorageForSide(@Nullable Direction direction);
}
