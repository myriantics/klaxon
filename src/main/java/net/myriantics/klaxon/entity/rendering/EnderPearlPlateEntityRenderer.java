package net.myriantics.klaxon.entity.rendering;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class EnderPearlPlateEntityRenderer extends MobEntityRenderer,  {
    public EnderPearlPlateEntityRenderer(EntityRendererFactory.Context context, EntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return null;
    }
}
