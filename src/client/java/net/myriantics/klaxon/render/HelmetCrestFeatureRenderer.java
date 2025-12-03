package net.myriantics.klaxon.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.render.model.HelmetCrestEntityModel;

public class HelmetCrestFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    private final HelmetCrestEntityModel<T> helmetCrestEntityModel;
    private final ArmorFeatureRenderer<T, M, A> armorFeatureRenderer;

    public HelmetCrestFeatureRenderer(FeatureRendererContext<T, M> context, ArmorFeatureRenderer<T, M, A> armorFeatureRenderer, HelmetCrestEntityModel<T> helmetCrestEntityModel) {
        super(context);
        this.armorFeatureRenderer = armorFeatureRenderer;
        this.helmetCrestEntityModel = helmetCrestEntityModel;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        this.helmetCrestEntityModel.render(matrices, vertexConsumers.getBuffer(this.helmetCrestEntityModel.getLayer()));
    }

    public void setVisible(boolean visible) {

    }
}
