package net.myriantics.klaxon.registry.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.entities.projectile.explosive_deepslate_chunk.ExplosiveDeepslateChunkEntity;
import net.myriantics.klaxon.entity.entities.projectile.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.entity.entities.mob.ominous_deepslate_blast_processor.OminousDeepslateBlastProcessorEntity;

public abstract class KlaxonEntityTypes {

    public static final Holder<EntityType<GrappleClawEntity>> GRAPPLE_CLAW = register(
            "grapple_claw",
            EntityType.Builder.<GrappleClawEntity>of(GrappleClawEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.8f)
                    .eyeHeight(0.4f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
    );
    public static final Holder<EntityType<OminousDeepslateBlastProcessorEntity>> OMINOUS_DEEPSLATE_BLAST_PROCESSOR = register(
            "ominous_deepslate_blast_processor",
            EntityType.Builder.of(OminousDeepslateBlastProcessorEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .eyeHeight(0.5f)
                    .clientTrackingRange(10)
    );
    public static final Holder<EntityType<ExplosiveDeepslateChunkEntity>> EXPLOSIVE_DEEPSLATE_CHUNK = register(
            "explosive_deepslate_chunk",
            EntityType.Builder.<ExplosiveDeepslateChunkEntity>of(ExplosiveDeepslateChunkEntity::new, MobCategory.MISC)
                    .sized(0.35f, 0.35f)
                    .eyeHeight(0.35f/2)
                    .clientTrackingRange(4)
                    .updateInterval(20)
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Entities!");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> Holder<EntityType<T>> register(String id, EntityType.Builder<T> type) {
        // built with null identifier to stop missing datafixer log message
        return (Holder<EntityType<T>>) (Object) Registry.registerForHolder(BuiltInRegistries.ENTITY_TYPE, KlaxonCommon.locate(id), type.build(null));
    }
}
