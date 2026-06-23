package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.filing_cabinet.FilingCabinetBlockEntity;
import net.myriantics.klaxon.block.machines.filing_cabinet.FilingCabinetDrawerBlock;
import net.myriantics.klaxon.util.container.KlaxonStorageProvider;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonItemStorages {

    static {
        register(KlaxonBlockEntityTypes.DEEPSLATE_BLAST_PROCESSOR);
        register(KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR);
        register(KlaxonBlockEntityTypes.FILING_CABINET);
        registerFilingCabinetDrawer(KlaxonBlocks.FILING_CABINET_DRAWER);
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

    private static void registerFilingCabinetDrawer(Holder<Block> drawerHolder) {
        if (drawerHolder.value() instanceof FilingCabinetDrawerBlock) {
            ItemStorage.SIDED.registerForBlocks((level, pos, state, blockEntity, context) -> {
                        @Nullable FilingCabinetBlockEntity cabinetBlockEntity = ((FilingCabinetDrawerBlock) state.getBlock()).findFilingCabinetBlockEntity(level, pos, state);
                        if (cabinetBlockEntity != null) {
                            return cabinetBlockEntity.getStorageForSide(context);
                        }
                        return null;
                    }, drawerHolder.value()
            );
        } else {
            throw new AssertionError("Expected a Filing Cabinet Drawer, got" + drawerHolder);
        }
    }
}
