package net.myriantics.klaxon.registry.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

    public static <T extends BlockEntity> BlockEntityType<T> register(String id, Block block, BlockEntityType.BlockEntitySupplier<T> factory) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(id), BlockEntityType.Builder.of(factory, block).build());
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(id), builder.build());
    }
}
