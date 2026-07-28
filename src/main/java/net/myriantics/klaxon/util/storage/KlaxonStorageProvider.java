package net.myriantics.klaxon.util.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface KlaxonStorageProvider<T extends TransferVariant<?>> {
    @Nullable Storage<T> getStorageForSide(@Nullable Direction direction);
}
