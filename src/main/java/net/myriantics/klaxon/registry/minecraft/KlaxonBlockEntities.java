package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;

public class KlaxonBlockEntities {
    public static final BlockEntityType<DeepslateBlastProcessorBlockEntity> DEEPSLATE_BLAST_PROCESSOR_BLOCK_ENTITY = register(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, DeepslateBlastProcessorBlockEntity::new);

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Block Entities!");
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(Block block, BlockEntityType.BlockEntityFactory<T> factory) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Registries.BLOCK.getId(block), BlockEntityType.Builder.create(factory, block).build());
    }
}
