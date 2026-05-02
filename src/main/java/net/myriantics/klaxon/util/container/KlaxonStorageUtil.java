package net.myriantics.klaxon.util.container;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class KlaxonStorageUtil {
    public static @Nullable Storage<ItemVariant> findStorage(Level level, BlockPos targetPos, @Nullable Direction targetFace) {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, targetPos, targetFace);
        if (storage == null) {
            List<Entity> containerEntities = level.getEntities((Entity) null, new AABB(targetPos), EntitySelector.CONTAINER_ENTITY_SELECTOR);
            return !containerEntities.isEmpty() ? InventoryStorage.of((Container)containerEntities.get(level.random.nextInt(containerEntities.size())), null) : null;
        }
        return storage;
    }
}
