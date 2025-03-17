package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;

public class KlaxonBlockEntities {
    public static final BlockEntityType<DeepslateBlastProcessorBlockEntity> DEEPSLATE_BLAST_PROCESSOR_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, toBlockEntity(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR),
                    FabricBlockEntityTypeBuilder.create(DeepslateBlastProcessorBlockEntity::new,
                            KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR).build());

    public static void registerBlockEntities() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's Block Entities");
    }

    public static String toBlockEntity(Block block) {
        return block.asItem().toString() + "_be";
    }
}
