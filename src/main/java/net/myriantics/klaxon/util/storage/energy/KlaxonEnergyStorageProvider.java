package net.myriantics.klaxon.util.storage.energy;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public interface KlaxonEnergyStorageProvider {
    @Nullable EnergyStorage getEnergyStorageForSide(@Nullable Direction direction);
}
