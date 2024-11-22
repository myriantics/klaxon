package net.myriantics.klaxon.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity;

public class KlaxonBlockEntities {
    public static final BlockEntityType<DeepslateBlastProcessorBlockEntity> DEEPSLATE_BLAST_PROCESSOR_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate("deepslate_blast_processor_be"),
                    FabricBlockEntityTypeBuilder.create(DeepslateBlastProcessorBlockEntity::new,
                            KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR).build());

    public static void registerBlockEntities() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's Block Entities");
    }

}
