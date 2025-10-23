package net.myriantics.klaxon.registry.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;

public abstract class KlaxonEntityTypes {

    public static final EntityType<GrappleClawEntity> STEEL_GRAPPLE_CLAW = register(
            "steel_grapple_claw",
            EntityType.Builder.<GrappleClawEntity>create(GrappleClawEntity::new, SpawnGroup.MISC)
                    .dimensions(0.8f, 0.8f)
                    .eyeHeight(0.4f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Entities!");
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        // built with null identifier to stop missing datafixer log message
        return Registry.register(Registries.ENTITY_TYPE, KlaxonCommon.locate(id), type.build(null));
    }
}
