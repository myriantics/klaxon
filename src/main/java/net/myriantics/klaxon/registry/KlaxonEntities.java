package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.EnderPlateEntity;

public class KlaxonEntities {
    public static final EntityType<EnderPlateEntity> ENDER_PEARL_PLATE_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            KlaxonCommon.locate("ender_plate_entity"),
            FabricEntityTypeBuilder.<EnderPlateEntity>create(SpawnGroup.MISC, EnderPlateEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build());

    public static void registerModEntities() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's Entities");
    }
}
