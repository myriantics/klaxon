package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;

public class KlaxonBlockEntities {
    public static final BlockEntityType<DeepslateBlastProcessorBlockEntity> DEEPSLATE_BLAST_PROCESSOR_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, toBlockEntity(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR),
                    BlockEntityType.Builder.create(DeepslateBlastProcessorBlockEntity::new,
                            KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR).build());

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Block Entities!");
    }

    public static String toBlockEntity(Block block) {
        return block.asItem().toString() + "_be";
    }
}
