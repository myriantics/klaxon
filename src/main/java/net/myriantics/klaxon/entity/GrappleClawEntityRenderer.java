package net.myriantics.klaxon.entity;

import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class GrappleClawEntityRenderer extends ProjectileEntityRenderer<GrappleClawEntity> {
    public GrappleClawEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(GrappleClawEntity entity) {
        return ArrowEntityRenderer.TEXTURE;
    }
}
