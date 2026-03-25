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
        offset = offset.relative(faceDir, 0.0001);
        poseStack.pushPose();

        float centerX = (float) (blockCenterPos.x - cameraPos.x);
        float centerY = (float) (blockCenterPos.y - cameraPos.y);
        float centerZ = (float) (blockCenterPos.z - cameraPos.z);

        poseStack.translate(centerX, centerY, centerZ);
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.scale(1f/16, 1f/16, 1f/16);

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

        state.getShape(level, pos).forAllBoxes((x, y, z, x1, y1, z1) -> {
            double i = clickedPos.x + 0.5;
            double j = clickedPos.y + 0.5;
            double k = clickedPos.z + 0.5;

            boolean win = switch (faceDir.getAxis()) {
                case X -> (testAllowance(i, x) || testAllowance(i, x1)) && isBetweenInc(j, y, y1) && isBetweenInc(k, z, z1);
                case Y -> (testAllowance(j, y) || testAllowance(j, y1)) && isBetweenInc(i, x, x1) && isBetweenInc(k, z, z1);
                case Z -> (testAllowance(k, z) || testAllowance(k, z1)) && isBetweenInc(i, x, x1) && isBetweenInc(j, y, y1);
            };

            if (win) {
                final float[] min = new float[2];
                final float[] max = new float[2];
                switch (faceDir) {
                    case UP -> {
                        min[0] = (float) (1 - x);
                        min[1] = (float) (1 - z);
                        max[0] = (float) (1 - x1);
                        max[1] = (float) (1 - z1);
                    }
                    case DOWN -> {
                        min[0] = (float) (1 - x1);
                        min[1] = (float) z;
                        max[0] = (float) (1 - x);
                        max[1] = (float) z1;
                    }
                    case NORTH -> {
                        min[0] = (float) x;
                        min[1] = (float) y;
                        max[0] = (float) x1;
                        max[1] = (float) y1;
                    }
                    case SOUTH -> {
                        min[0] = (float) (1 - x1);
                        min[1] = (float) y;
                        max[0] = (float) (1 - x);
                        max[1] = (float) y1;
                    }
                    case EAST -> {
                        min[0] = (float) z;
                        min[1] = (float) y;
                        max[0] = (float) z1;
                        max[1] = (float) y1;
                    }
                    case WEST -> {
                        min[0] = (float) (1f - z1);
                        min[1] = (float) y;
                        max[0] = (float) (1f - z);
                        max[1] = (float) y1;
                    }
                }

                min[0] *= 16;
                min[1] *= 16;
                max[0] *= 16;
                max[1] *= 16;

                int light = LightTexture.pack(15, 15);

                this.vertex(pose, consumer, min[0] - 8,max[1] - 8, 0, min[0]/16 ,min[1]/16, 0, 0, 0, light);
                this.vertex(pose, consumer, max[0] - 8,max[1] - 8, 0, max[0]/16 ,min[1]/16, 0, 0, 0, light);
                this.vertex(pose, consumer, max[0] - 8, min[1] - 8, 0, max[0]/16 ,max[1]/16, 0, 0, 0, light);
                this.vertex(pose, consumer, min[0] - 8,min[1] - 8, 0, min[0]/16 , max[1]/16, 0, 0, 0, light);
            }
        });


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

    private boolean isBetweenInc(double val, double min, double max) {
        return val >= min && val <= max;
    }

    private boolean testAllowance(double a, double b) {
        return a >= b - 0.01 && a <= b + 0.01;
    }
}
