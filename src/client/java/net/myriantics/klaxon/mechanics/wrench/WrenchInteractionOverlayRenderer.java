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
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.registry.render.KlaxonTextures;

import java.util.ArrayList;

public class WrenchInteractionOverlayRenderer {

    private static final ResourceLocation TEXTURE = KlaxonTextures.decorate(KlaxonTextures.WRENCH_OVERLAY_TEST_ARROW);

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
        // poseStack.scale(1f/16, 1f/16, 1f/16);

        poseStack.mulPose(faceDir.getRotation());
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.XN.rotationDegrees(90));
        /*
        switch (faceDir) {
            case UP -> poseStack.mulPose(Axis.ZP.rotationDegrees(player.getMotionDirection().toYRot() + 180));
            case DOWN -> poseStack.mulPose(Axis.ZN.rotationDegrees(player.getMotionDirection().toYRot()));
        }
         */

        PoseStack.Pose pose = poseStack.last();

        BlockFaceGroup group = new BlockFaceGroup(faceDir, clickedPos.add(0.5f, 0.5f, 0.5f).toVector3f());

        state.getShape(level, pos).forAllEdges(group::tryAdd);

        int light = LightTexture.pack(15, 15);
        group.renderSelected(pose, consumer, light);

        poseStack.popPose();
    }

    private void vertex(
            PoseStack.Pose poseStack, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, float normalX, float normalZ, float normalY, int light
    ) {
        vertexConsumer.addVertex(poseStack, x, y, z)
                .setColor(CommonColors.WHITE)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(poseStack, normalX, normalY, normalZ);
    }


}
