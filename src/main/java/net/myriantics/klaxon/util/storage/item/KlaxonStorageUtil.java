package net.myriantics.klaxon.util.storage.item;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mixin.minecraft.storage.HopperBlockEntityInvoker;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonStorageUtil {

    public static @Nullable Storage<ItemVariant> findStorage(Level level, BlockPos targetPos, @Nullable Direction targetFace) {
        return findStorage(level, targetPos, targetFace, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
    }

    public static @Nullable Storage<ItemVariant> findStorage(Level level, BlockPos targetPos, @Nullable Direction targetFace, double x, double y, double z) {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, targetPos, targetFace);
        if (storage == null) {
            @Nullable Container container = HopperBlockEntityInvoker.klaxon$invokeGetEntityContainer(level, x, y, z);
            return container == null ? null : InventoryStorage.of(container, null);
        }
        return storage;
    }
}
