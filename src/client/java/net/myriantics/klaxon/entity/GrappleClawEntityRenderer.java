package net.myriantics.klaxon.entity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.registry.render.KlaxonTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.HashMap;

public class GrappleClawEntityRenderer extends EntityRenderer<GrappleClawEntity> {
    public GrappleClawEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private final ResourceLocation DEFAULT_STEEL_GRAPPLE_CLAW_TEXTURE = KlaxonTextures.decorate(KlaxonTextures.STEEL_GRAPPLE_CLAW_ENTITY);
    private final HashMap<Item, ResourceLocation> ITEM_2_TEXTURE_MAP = new HashMap<>();

    @Override
    public ResourceLocation getTextureLocation(GrappleClawEntity entity) {
        ItemStack stack = entity.getPickupItemStackOrigin();
        if (stack.isEmpty()) {
            return DEFAULT_STEEL_GRAPPLE_CLAW_TEXTURE;
        }
        Item item = stack.getItem();

        // add new texture id to the map if it isn't present
        if (!ITEM_2_TEXTURE_MAP.containsKey(item)) {
            ITEM_2_TEXTURE_MAP.put(item, KlaxonTextures.copyDir(
                    BuiltInRegistries.ITEM.getKey(item).getPath() + ".png",
                    DEFAULT_STEEL_GRAPPLE_CLAW_TEXTURE
            ));
        }

        return ITEM_2_TEXTURE_MAP.get(item);
    }

    @Override
    public void render(GrappleClawEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        matrices.mulPose(
                Axis.YP.rotationDegrees(Mth.lerp(tickDelta, entity.yRotO, entity.getYRot()) - 90.0F)
        );
        matrices.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(tickDelta, entity.xRotO, entity.getXRot())));


        float s = entity.shakeTime - tickDelta;
        if (s > 0.0F) {
            float t = -Mth.sin(s * 3.0F) * s;
            matrices.mulPose(Axis.ZP.rotationDegrees(t));
        }

        // rotate the claw 45 degrees
        matrices.mulPose(Axis.XP.rotationDegrees(45.0F));
        matrices.scale(0.05625F, 0.05625F, 0.05625F);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
        PoseStack.Pose entry = matrices.last();

        float crossbeamMinU = 16f/32;
        float crossbeamMaxU = 28f/32;
        float crossbeamMinV = 0f/32;
        float crossbeamMaxV = 12f/32;

        // crossbeam at end
        this.vertex(entry, vertexConsumer, 6, -6, -6, crossbeamMinU, crossbeamMinV, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, -6, 6, crossbeamMaxU, crossbeamMinV, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, 6, 6, crossbeamMaxU, crossbeamMaxV, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, 6, -6, crossbeamMinU, crossbeamMaxV, -1, 0, 0, light);

        this.vertex(entry, vertexConsumer, 6, 6, -6, crossbeamMinU, crossbeamMinV, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, 6, 6, crossbeamMaxU, crossbeamMinV, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, -6, 6, crossbeamMaxU, crossbeamMaxV, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, 6, -6, -6, crossbeamMinU, crossbeamMaxV, 1, 0, 0, light);

        float nubMinU = 0f/32;
        float nubMaxU = 4f/32;
        float nubMinV = 12f/32;
        float nubMaxV = 16f/32;

        // small nub at base
        this.vertex(entry, vertexConsumer, -4, -2, -2, nubMinU, nubMinV, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, -2, 2, nubMaxU, nubMinV, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, 2, 2, nubMaxU, nubMaxV, -1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, 2, -2, nubMinU, nubMaxV, -1, 0, 0, light);

        this.vertex(entry, vertexConsumer, -4, 2, -2, nubMinU, nubMinV, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, 2, 2, nubMaxU, nubMinV, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, -2, 2, nubMaxU, nubMaxV, 1, 0, 0, light);
        this.vertex(entry, vertexConsumer, -4, -2, -2, nubMinU, nubMaxV, 1, 0, 0, light);

        float clawMinU = 0f/32;
        float clawMaxU = 14f/32;
        float clawMinV = 0f/32;
        float clawMaxV = 12f/32;

        // main claw
        for (int u = 0; u < 4; u++) {
            matrices.mulPose(Axis.XP.rotationDegrees(90.0F));

            this.vertex(entry, vertexConsumer, -6, -6, 0, clawMinU, clawMinV, 0, 1, 0, light);
            this.vertex(entry, vertexConsumer, 8, -6, 0, clawMaxU, clawMinV, 0, 1, 0, light);
            this.vertex(entry, vertexConsumer, 8, 6, 0, clawMaxU, clawMaxV, 0, 1, 0, light);
            this.vertex(entry, vertexConsumer, -6, 6, 0, clawMinU, clawMaxV, 0, 1, 0, light);
        }

        matrices.popPose();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Vec3 getRenderOffset(GrappleClawEntity entity, float tickDelta) {
        return entity.hasHookedEntity()
                ? new Vec3(0, entity.getEyeHeight(entity.getPose()), 0)
                : super.getRenderOffset(entity, tickDelta);
    }

    private void vertex(
            PoseStack.Pose matrix, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light
    ) {
        vertexConsumer.addVertex(matrix, (float)x, (float)y, (float)z)
                .setColor(CommonColors.WHITE)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(matrix, normalX, normalY, normalZ);
    }
}
