package net.myriantics.klaxon.registry.render;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.ExplosiveDeepslateChunkRenderer;
import net.myriantics.klaxon.entity.GrappleClawEntityRenderer;
import net.myriantics.klaxon.entity.OminousDeepslateBlastProcessorEntityRenderer;
import net.myriantics.klaxon.entity.entities.projectile.explosive_deepslate_chunk.ExplosiveDeepslateChunkEntity;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;

public abstract class KlaxonEntityRenderers {

    static {
        register(KlaxonEntityTypes.GRAPPLE_CLAW, GrappleClawEntityRenderer::new);
        register(KlaxonEntityTypes.OMINOUS_DEEPSLATE_BLAST_PROCESSOR, OminousDeepslateBlastProcessorEntityRenderer::new);
        register(KlaxonEntityTypes.EXPLOSIVE_DEEPSLATE_CHUNK, ExplosiveDeepslateChunkRenderer::new);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Entity Renderers!");
    }

    private static <T extends Entity> void register(Holder<EntityType<T>> typeHolder, EntityRendererProvider<T> provider) {
        register(typeHolder.value(), provider);
    }

    private static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider) {
        EntityRendererRegistry.register(type, provider);
    }
}
