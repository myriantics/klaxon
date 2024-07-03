package net.myriantics.klaxon.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonMain;
import net.myriantics.klaxon.block.blockentities.BlastChamberBlockEntity;

public class KlaxonBlockEntities {
    public static final BlockEntityType<BlastChamberBlockEntity> BLAST_CHAMBER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(KlaxonMain.MOD_ID, "crude_extrapolator_be"),
                    FabricBlockEntityTypeBuilder.create(BlastChamberBlockEntity::new,
                            KlaxonBlocks.DEEPSLATE_BLAST_CHAMBER).build());

    public static void registerBlockEntities() {
        KlaxonMain.LOGGER.info("Registering Klaxon's Block Entities");
    }

}
