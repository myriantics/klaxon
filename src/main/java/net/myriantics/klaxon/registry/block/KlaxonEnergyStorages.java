package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.util.storage.KlaxonStorageProvider;
import net.myriantics.klaxon.util.storage.energy.KlaxonEnergyStorageProvider;
import team.reborn.energy.api.EnergyStorage;

public abstract class KlaxonEnergyStorages {
    static {
        register(KlaxonBlockEntityTypes.CREATIVE_POWER_BANK);
        register(KlaxonBlockEntityTypes.ENERGY_SINK);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Energy Storages!");
    }

    private static <T extends BlockEntity & KlaxonEnergyStorageProvider> void register(Holder<BlockEntityType<T>> blockEntityTypeHolder) {
        register(blockEntityTypeHolder.value());
    }

    private static <T extends BlockEntity & KlaxonEnergyStorageProvider> void register(BlockEntityType<T> blockEntityType) {
        EnergyStorage.SIDED.registerForBlockEntity(KlaxonEnergyStorageProvider::getStorageForSide, blockEntityType);
    }
}
