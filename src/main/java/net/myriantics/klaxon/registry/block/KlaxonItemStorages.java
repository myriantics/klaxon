package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorBlockEntity;

public abstract class KlaxonItemStorages {

    static {
        ItemStorage.SIDED.registerForBlockEntity(DeepslateBlastProcessorBlockEntity::storageProvider, KlaxonBlockEntityTypes.DEEPSLATE_BLAST_PROCESSOR.value());
        ItemStorage.SIDED.registerForBlockEntity(SteelBlastProcessorBlockEntity::storageProvider, KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR.value());
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Item Storages!");
    }
}
