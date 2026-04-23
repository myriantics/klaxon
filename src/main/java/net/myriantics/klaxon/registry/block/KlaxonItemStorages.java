package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.KlaxonBaseContainerBlockEntity;

public abstract class KlaxonItemStorages {

    static {
        register(KlaxonBlockEntityTypes.DEEPSLATE_BLAST_PROCESSOR);
        register(KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Item Storages!");
    }

    private static <T extends KlaxonBaseContainerBlockEntity> void register(Holder<BlockEntityType<T>> blockEntityTypeHolder) {
        register(blockEntityTypeHolder.value());
    }

    private static <T extends KlaxonBaseContainerBlockEntity> void register(BlockEntityType<T> blockEntityType) {
        ItemStorage.SIDED.registerForBlockEntity(KlaxonBaseContainerBlockEntity::getStorageForSide, blockEntityType);
    }
}
