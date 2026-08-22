package net.myriantics.klaxon.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.entity.entities.projectile.explosive_deepslate_chunk.ExplosiveDeepslateChunkEntity;

public class ExplosiveDeepslateChunkRenderer extends EntityRenderer<ExplosiveDeepslateChunkEntity> {
    public ExplosiveDeepslateChunkRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ExplosiveDeepslateChunkEntity entity) {
        return null;
    }
}
