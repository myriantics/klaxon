package net.myriantics.klaxon.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.render.KlaxonTextures;

import java.util.HashMap;

public class GrappleClawEntityRenderer extends EntityRenderer<GrappleClawEntity> {
    public GrappleClawEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    private final Identifier DEFAULT_STEEL_GRAPPLE_CLAW_TEXTURE = KlaxonTextures.STEEL_GRAPPLE_CLAW_ENTITY.withPath((path) -> "textures/" + path + ".png");
    private final HashMap<Item, Identifier> ITEM_2_TEXTURE_MAP = new HashMap<>();

    @Override
    public Identifier getTexture(GrappleClawEntity entity) {
        Item item = entity.getItemStack().getItem();
        if (item == null || item.equals(Items.AIR)) {
            return DEFAULT_STEEL_GRAPPLE_CLAW_TEXTURE;
        }

        // add new texture id to the map if it isn't present
        if (!ITEM_2_TEXTURE_MAP.containsKey(item)) {
            ITEM_2_TEXTURE_MAP.put(item, KlaxonTextures.copyDir(
                    Registries.ITEM.getId(item).getPath() + ".png",
                    DEFAULT_STEEL_GRAPPLE_CLAW_TEXTURE
            ));
        }

        return ITEM_2_TEXTURE_MAP.get(item);
    }

    @Override
    public void render(GrappleClawEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(
                RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90.0F)
        );
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch())));


        float crossbeamMinU = 16f/32;
        float crossbeamMaxU = 28f/32;
        float crossbeamMinV = 0f/32;
        float crossbeamMaxV = 12f/32;

        float s = entity.shake - tickDelta;
        if (s > 0.0F) {
            float t = -MathHelper.sin(s * 3.0F) * s;
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(t));
        }

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));
        matrices.scale(0.05625F, 0.05625F, 0.05625F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(this.getTexture(entity)));
        MatrixStack.Entry entry = matrices.peek();

        // crossbeam at end
        this.vertex(entry, vertexConsumer, 6, -6, -6, crossbeamMinU, crossbeamMinV, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, -6, 6, crossbeamMaxU, crossbeamMinV, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, 6, 6, crossbeamMaxU, crossbeamMaxV, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, 6, -6, crossbeamMinU, crossbeamMaxV, -1, 0, 0, light);

        this.vertex(entry, vertexConsumer, 6, 6, -6, crossbeamMinU, crossbeamMinV, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, 6, 6, crossbeamMaxU, crossbeamMinV, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, -6, 6, crossbeamMaxU, crossbeamMaxV, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, -6, -6, crossbeamMinU, crossbeamMaxV, 1, 0, 0, light);


        // small nub at base
        this.vertex(entry, vertexConsumer, -4, -2, -2, 0.0F, 12f/32, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, -2, 2, 4f/32, 12f/32, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, 2, 2, 4f/32, 16f/32, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, 2, -2, 0.0F, 16f/32, -1, 0, 0, light);

        this.vertex(entry, vertexConsumer, -4, 2, -2, 0.0F, 12f/32, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, 2, 2, 4f/32, 12f/32, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, -2, 2, 4f/32, 16f/32, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, -2, -2, 0.0F, 16f/32, 1, 0, 0, light);

        // main claw
        for (int u = 0; u < 4; u++) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));

            this.vertex(entry, vertexConsumer, -6, -6, 0, 0.0F, 0.0F, 0, 1, 0, light);
            this.vertex(entry, vertexConsumer, 8, -6, 0, 14f/32, 0.0F, 0, 1, 0, light);
            this.vertex(entry, vertexConsumer, 8, 6, 0, 14f/32, 12f/32, 0, 1, 0, light);
            this.vertex(entry, vertexConsumer, -6, 6, 0, 0.0F, 12f/32, 0, 1, 0, light);
        }

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void vertex(
            MatrixStack.Entry matrix, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light
    ) {
        vertexConsumer.vertex(matrix, (float)x, (float)y, (float)z)
                .color(Colors.WHITE)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(matrix, normalX, normalY, normalZ);
    }
}
