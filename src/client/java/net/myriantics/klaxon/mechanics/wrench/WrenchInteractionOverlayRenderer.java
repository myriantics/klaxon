package net.myriantics.klaxon.mechanics.wrench;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.registry.render.KlaxonTextures;
import net.myriantics.klaxon.util.BlockFaceRegion;
import org.jetbrains.annotations.NotNull;

public class WrenchInteractionOverlayRenderer {

    private static final ResourceLocation TEXTURE = KlaxonTextures.decorate(KlaxonTextures.WRENCH_OVERLAY_9SLICE);

    public boolean shouldRender() {
        return true;
    }

    public void render(ClientLevel level, WrenchActionContext.Manual context, Camera camera, DeltaTracker deltaTracker, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(TEXTURE));
        BlockState state = context.getTargetState();
        BlockPos pos = context.getTargetPos();
        Direction faceDir = context.getHitResult().getDirection();
        Player player = context.getPlayer();
        Vec3 blockCenterPos = pos.getCenter();
        Vec3 clickedPos = context.getHitResult().getLocation().subtract(blockCenterPos);
        Vec3 cameraPos = camera.getPosition();

        Vec3 offset = clickedPos.multiply(Math.abs(faceDir.getStepX()), Math.abs(faceDir.getStepY()), Math.abs(faceDir.getStepZ()));
        // Vec3 offset = clickedPos.multiply(faceDir.getNormal().getX(), faceDir.getNormal().getY(), faceDir.getNormal().getZ());
        // go go gadget anti centerZ fighting
        offset = offset.relative(faceDir, 0.005);
        poseStack.pushPose();

        float centerX = (float) (blockCenterPos.x - cameraPos.x);
        float centerY = (float) (blockCenterPos.y - cameraPos.y);
        float centerZ = (float) (blockCenterPos.z - cameraPos.z);

        poseStack.translate(centerX, centerY, centerZ);
        poseStack.translate(offset.x, offset.y, offset.z);
        // pose.scale(1f/16, 1f/16, 1f/16);

        poseStack.mulPose(faceDir.getRotation());
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.XN.rotationDegrees(90));

        PoseStack.Pose pose = poseStack.last();

        SelectedFaceCalculator calculator = new SelectedFaceCalculator(faceDir, clickedPos.add(0.5f, 0.5f, 0.5f).toVector3f());

        state.getShape(level, pos).forAllEdges(calculator::tryAdd);

        int color = 0xB000FF00;
        int light = LightTexture.pack(15, 15);
        renderBlockFaceRegion(calculator.get(), pose, consumer, color, light);

        poseStack.popPose();
    }

    private void renderBlockFaceRegion(@NotNull BlockFaceRegion region, PoseStack.Pose pose, VertexConsumer consumer, int color, int light) {
        final float texCornerSliceLength = 2f/16;

        final float cornerSliceWidth = Math.min(texCornerSliceLength, region.width() / 2);
        final float cornerSliceHeight = Math.min(texCornerSliceLength, region.height() / 2);
        final float sideSliceWidth = Math.clamp(region.width() - (cornerSliceWidth * 2), 0, 1 - (texCornerSliceLength * 2));
        final float sideSliceHeight = Math.clamp(region.height() - (cornerSliceHeight * 2), 0, 1 - (texCornerSliceLength * 2));

        // top left - GOOD
        quad(pose, consumer, region.minX, region.maxY - cornerSliceHeight, region.minX + cornerSliceWidth, region.maxY, 0, 0, cornerSliceWidth, cornerSliceHeight, color, light);
        // top right - GOOD
        quad(pose, consumer, region.maxX - cornerSliceWidth, region.maxY - cornerSliceHeight, region.maxX, region.maxY, 1f - cornerSliceWidth, 0, 1f, cornerSliceHeight, color, light);
        // bottom left - GOOD
        quad(pose, consumer, region.minX, region.minY, region.minX + cornerSliceWidth, region.minY + cornerSliceHeight, 0, 1f - cornerSliceHeight, cornerSliceWidth, 1f, color, light);
        // bottom right - GOOD
        quad(pose, consumer, region.maxX - cornerSliceWidth, region.minY, region.maxX, region.minY + cornerSliceHeight, 1f - cornerSliceWidth, 1f - cornerSliceHeight, 1f, 1f, color, light);

        if (sideSliceHeight > 0) {
            // left side - GOOD
            quad(pose, consumer, region.minX, region.minY + cornerSliceHeight, region.minX + cornerSliceWidth, region.maxY - cornerSliceHeight, 0, cornerSliceHeight, cornerSliceWidth, cornerSliceHeight + sideSliceHeight, color, light);
            // right side - GOOD
            quad(pose, consumer, region.maxX - cornerSliceWidth, region.minY + cornerSliceHeight, region.maxX, region.maxY - cornerSliceHeight, 1f - cornerSliceWidth, cornerSliceHeight, 1, cornerSliceHeight + sideSliceHeight, color, light);
        }
        if (sideSliceWidth > 0) {
            // bottom side
            quad(pose, consumer, region.minX + cornerSliceWidth, region.minY, region.maxX - cornerSliceWidth, region.minY + cornerSliceHeight, cornerSliceWidth, 1 - cornerSliceHeight, cornerSliceWidth + sideSliceWidth, 1, color, light);
            // top side
            quad(pose, consumer, region.minX + cornerSliceWidth, region.maxY - cornerSliceHeight, region.maxX  - cornerSliceWidth, region.maxY, cornerSliceWidth, 0, cornerSliceWidth + sideSliceWidth, cornerSliceHeight, color, light);
        }
        if (sideSliceWidth > 0 && sideSliceHeight > 0) {
            quad(pose, consumer, region.minX + cornerSliceWidth, region.minY + cornerSliceHeight, region.maxX - cornerSliceWidth, region.maxY - cornerSliceWidth, texCornerSliceLength, texCornerSliceLength, texCornerSliceLength + sideSliceWidth, texCornerSliceLength + sideSliceHeight, color, light);
        }
    }

    private void quad(PoseStack.Pose poseStack, VertexConsumer vertexConsumer, float xMin, float yMin, float xMax, float yMax, float uMin, float vMin, float uMax, float vMax, int color, int light) {
        vertex(poseStack, vertexConsumer, xMin - 0.5f, yMax - 0.5f, 0, uMin, vMin, 0, 0, 0, color, light);
        vertex(poseStack, vertexConsumer, xMax - 0.5f, yMax - 0.5f, 0, uMax, vMin, 0, 0, 0, color, light);
        vertex(poseStack, vertexConsumer, xMax - 0.5f, yMin - 0.5f, 0, uMax, vMax, 0, 0, 0, color, light);
        vertex(poseStack, vertexConsumer, xMin - 0.5f, yMin - 0.5f, 0, uMin, vMax, 0, 0, 0, color, light);
    }

    private void vertex(
            PoseStack.Pose poseStack, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, float normalX, float normalZ, float normalY, int color, int light
    ) {
        vertexConsumer.addVertex(poseStack, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(poseStack, normalX, normalY, normalZ);
    }
}
