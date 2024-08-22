package net.myriantics.klaxon.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonEntities {
    public static final EntityType<EnderPlateEntity> ENDER_PEARL_PLATE_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(KlaxonCommon.MOD_ID, "ender_plate"),
            FabricEntityTypeBuilder.<EnderPlateEntity>create(SpawnGroup.MISC, EnderPlateEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build());

    public static void registerModEntities() {
        KlaxonCommon.LOGGER.info("Registered Klaxon's Entities");
    }
}
