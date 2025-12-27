package net.myriantics.klaxon.render;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.myriantics.klaxon.registry.KlaxonEntityModelLayers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.render.KlaxonTextures;
import net.myriantics.klaxon.render.model.HelmetCrestEntityModel;

public class HelmetCrestFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    private final float scaleX;
    private final float scaleY;
    private final float scaleZ;
    private final HelmetCrestEntityModel<T> helmetCrestEntityModel;
    private static final Identifier TEXTURE = KlaxonTextures.decorate(KlaxonTextures.HELMET_CREST);

    public HelmetCrestFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader modelLoader) {
        this(context, modelLoader, 1.0f, 1.0f, 1.0f);
    }

    public HelmetCrestFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader modelLoader, float scaleX, float scaleY, float scaleZ) {
        super(context);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.helmetCrestEntityModel = new HelmetCrestEntityModel<>(modelLoader.getModelPart(KlaxonEntityModelLayers.HELMET_CREST));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T livingEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack helmetStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);

        if (helmetStack.contains(KlaxonDataComponentTypes.HELMET_CREST_COMPONENT)) {
            int crestColor = helmetStack.contains(DataComponentTypes.DYED_COLOR)
                    ? helmetStack.get(DataComponentTypes.DYED_COLOR).rgb()
                    : helmetStack.get(KlaxonDataComponentTypes.HELMET_CREST_COMPONENT);

            matrices.push();
            matrices.scale(this.scaleX, this.scaleY, this.scaleZ);

            boolean villager = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
            if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
                float m = 2.0F;
                float n = 1.4F;
                matrices.translate(0f, 1f/32, 0f);
                matrices.scale(0.7f, 0.7f, 0.7f);
                matrices.translate(0.0f, 1.0f, 0.0f);
            }

            // this is kind of ugly but generics are fucked so whatevs
            if (this.getContextModel() instanceof ModelWithHead modelWithHead) {
                modelWithHead.getHead().rotate(matrices);
            }

            VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(
                    vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE), helmetStack.hasGlint()
            );

            translate(matrices, villager);
            this.helmetCrestEntityModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, crestColor);

            matrices.pop();
        }
    }

    public static void translate(MatrixStack matrices, boolean villager) {
        float f = 0.625F;
        matrices.translate(0.0F, -0.25F, 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrices.scale(0.625F, -0.625F, -0.625F);
        if (villager) {
            matrices.translate(0.0F, 0.1875F, 0.0F);
        }
    }
}
