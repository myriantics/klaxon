package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.util.storage.KlaxonStorageProvider;

public abstract class KlaxonItemStorages {

    static {
        register(KlaxonBlockEntityTypes.DEEPSLATE_BLAST_PROCESSOR);
        register(KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR);
        register(KlaxonBlockEntityTypes.TURBINE_GENERATOR);
        register(KlaxonBlockEntityTypes.FURNACE_GENERATOR);
        register(KlaxonBlockEntityTypes.PRECISION_DISPENSER);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Item Storages!");
    }

    private static <T extends BlockEntity & KlaxonStorageProvider<ItemVariant>> void register(Holder<BlockEntityType<T>> blockEntityTypeHolder) {
        register(blockEntityTypeHolder.value());
    }

    private static <T extends BlockEntity & KlaxonStorageProvider<ItemVariant>> void register(BlockEntityType<T> blockEntityType) {
        ItemStorage.SIDED.registerForBlockEntity(KlaxonStorageProvider::getStorageForSide, blockEntityType);
    }
}
