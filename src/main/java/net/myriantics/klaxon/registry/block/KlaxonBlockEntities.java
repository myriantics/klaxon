package net.myriantics.klaxon.registry.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;

public abstract class KlaxonBlockEntities {
    public static final BlockEntityType<DeepslateBlastProcessorBlockEntity> DEEPSLATE_BLAST_PROCESSOR_BLOCK_ENTITY = register(
            "deepslate_blast_processor",
            KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR,
            DeepslateBlastProcessorBlockEntity::new
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Block Entities!");
        BlockEntityType.SIGN.addSupportedBlock(KlaxonBlocks.HALLNOX_SIGN);
        BlockEntityType.SIGN.addSupportedBlock(KlaxonBlocks.HALLNOX_WALL_SIGN);
        BlockEntityType.HANGING_SIGN.addSupportedBlock(KlaxonBlocks.HALLNOX_HANGING_SIGN);
        BlockEntityType.HANGING_SIGN.addSupportedBlock(KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN);
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String id, Block block, BlockEntityType.BlockEntityFactory<T> factory) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(id), BlockEntityType.Builder.create(factory, block).build());
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(id), builder.build());
    }
}
