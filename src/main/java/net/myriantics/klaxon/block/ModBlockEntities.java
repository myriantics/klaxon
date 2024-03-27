package net.myriantics.klaxon.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonMain;
import net.myriantics.klaxon.block.blockentities.CrudeExtrapolatorBlockEntity;

public class ModBlockEntities {
    public static final BlockEntityType<CrudeExtrapolatorBlockEntity> CRUDE_EXTRAPOLATOR_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(KlaxonMain.MOD_ID, "crude_extrapolator_be"),
                    FabricBlockEntityTypeBuilder.create(CrudeExtrapolatorBlockEntity::new,
                            ModBlocks.CRUDE_EXTRAPOLATOR).build());

    public static void registerBlockEntities() {
        KlaxonMain.LOGGER.info("Registering Klaxon's Block Entities");
    }

}
