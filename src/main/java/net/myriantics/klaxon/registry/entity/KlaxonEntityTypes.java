package net.myriantics.klaxon.registry.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;

public abstract class KlaxonEntityTypes {

    public static final EntityType<GrappleClawEntity> GRAPPLE_CLAW = register(
            "grapple_claw",
            EntityType.Builder.<GrappleClawEntity>of(GrappleClawEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.8f)
                    .eyeHeight(0.4f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Entities!");
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        // built with null identifier to stop missing datafixer log message
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, KlaxonCommon.locate(id), type.build(null));
    }
}
