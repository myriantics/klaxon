package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.decor.custom_hanging_sign.CustomHangingSignBlockEntity;
import net.myriantics.klaxon.block.customblocks.decor.custom_sign.CustomSignBlockEntity;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;

public class KlaxonBlockEntities {
    public static final BlockEntityType<DeepslateBlastProcessorBlockEntity> DEEPSLATE_BLAST_PROCESSOR_BLOCK_ENTITY = register(
            "deepslate_blast_processor",
            KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR,
            DeepslateBlastProcessorBlockEntity::new
    );
    public static final BlockEntityType<CustomSignBlockEntity> CUSTOM_SIGN_BLOCK_ENTITY = register(
            "sign",
            BlockEntityType.Builder.create(
                    CustomSignBlockEntity::new,
                    KlaxonBlocks.HALLNOX_SIGN,
                    KlaxonBlocks.HALLNOX_WALL_SIGN
            )
    );
    public static final BlockEntityType<CustomHangingSignBlockEntity> CUSTOM_HANGING_SIGN_BLOCK_ENTITY = register(
            "hanging_sign",
            BlockEntityType.Builder.create(
                    CustomHangingSignBlockEntity::new,
                    KlaxonBlocks.HALLNOX_HANGING_SIGN,
                    KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN
            )
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Block Entities!");
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String id, Block block, BlockEntityType.BlockEntityFactory<T> factory) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(id), BlockEntityType.Builder.create(factory, block).build());
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(id), builder.build());
    }
}
