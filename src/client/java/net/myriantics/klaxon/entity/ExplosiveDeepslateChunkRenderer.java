package net.myriantics.klaxon.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.entities.projectile.explosive_deepslate_chunk.ExplosiveDeepslateChunkEntity;
import net.myriantics.klaxon.registry.render.KlaxonEntityModelLayers;
import net.myriantics.klaxon.render.model.ExplosiveDeepslateChunkEntityModel;
import net.myriantics.klaxon.render.model.HelmetCrestEntityModel;

public class ExplosiveDeepslateChunkRenderer extends EntityRenderer<ExplosiveDeepslateChunkEntity> {

    protected static final ResourceLocation TEXTURE_ID = KlaxonCommon.locate("textures/entity/explosive_deepslate_chunk.png");

    protected final EntityModel<ExplosiveDeepslateChunkEntity> model;
    protected final RenderType renderType;

    public ExplosiveDeepslateChunkRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ExplosiveDeepslateChunkEntityModel(Minecraft.getInstance().getEntityModels().bakeLayer(KlaxonEntityModelLayers.EXPLOSIVE_DEEPSLATE_CHUNK));
        this.renderType = model.renderType(TEXTURE_ID);
    }

    @Override
    public void render(ExplosiveDeepslateChunkEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.tickCount > 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            poseStack.pushPose();
            poseStack.translate(0, 0.25, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
            this.model.renderToBuffer(poseStack, bufferSource.getBuffer(this.renderType), packedLight, OverlayTexture.NO_OVERLAY, entity.getColor());
            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ExplosiveDeepslateChunkEntity entity) {
        return TEXTURE_ID;
    }
}
