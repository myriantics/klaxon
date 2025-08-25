package net.myriantics.klaxon.util.grapple_winch;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.myriantics.klaxon.client.GrappleWinchConnectionManager;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public abstract class GrappleWinchCableRenderer {

    public static void renderGrappleWinchCable(Entity source, Vec3d playerPos, Vec3d clawPos, @Nullable Camera camera, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int blockLight) {
        matrices.push();
        double d = source.lerpYaw(tickDelta) * (float) (Math.PI / 180.0) + (Math.PI / 2);
        Vec3d vec3d2 = source.getLeashOffset(tickDelta);
        double e = Math.cos(d) * vec3d2.z + Math.sin(d) * vec3d2.x;
        double f = Math.sin(d) * vec3d2.z - Math.cos(d) * vec3d2.x;

        if (source instanceof PlayerEntity) {
            playerPos = source.getPos();
        }
        if (source instanceof GrappleClawEntity) {
            clawPos = source.getPos();
        }

        double g = clawPos.getX() + e;
        double h = clawPos.getY() + vec3d2.y;
        double i = clawPos.getZ() + f;
        matrices.translate(e, vec3d2.y, f);
        float j = (float)(playerPos.x - g);
        float k = (float)(playerPos.y - h);
        float l = (float)(playerPos.z - i);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float n = MathHelper.inverseSqrt(j * j + l * l) * 0.025F / 2.0F;
        float o = l * n;
        float p = j * n;
        BlockPos blockPos = BlockPos.ofFloored(source.getCameraPosVec(tickDelta));
        BlockPos blockPos2 = BlockPos.ofFloored(playerPos);
        int s = source.getWorld().getLightLevel(LightType.SKY, blockPos);
        int t = source.getWorld().getLightLevel(LightType.SKY, blockPos2);


        for (int u = 0; u <= 24; u++) {
            renderCableSegment(vertexConsumer, matrix4f, j, k, l, blockLight, blockLight, s, t, 0.025F, 0.025F, o, p, u, false);
        }

        for (int u = 24; u >= 0; u--) {
            renderCableSegment(vertexConsumer, matrix4f, j, k, l, blockLight, blockLight, s, t, 0.025F, 0.0F, o, p, u, true);
        }

        matrices.pop();
    }

    private static void renderCableSegment(
            VertexConsumer vertexConsumer,
            Matrix4f matrix,
            float grappleClawX,
            float grappleClawY,
            float grappleClawZ,
            int grappleClawBlockLight,
            int winchWielderBlockLight,
            int grappleClawSkyLight,
            int winchWielderSkyLight,
            float f,
            float winchWielderX,
            float winchWielderY,
            float winchWielderZ,
            int segmentIndex,
            boolean isLeashKnot
    ) {
        float segmentProportion = segmentIndex / 24.0F;
        int blockLight = (int)MathHelper.lerp(segmentProportion, (float)grappleClawBlockLight, (float)winchWielderBlockLight);
        int skyLight = (int)MathHelper.lerp(segmentProportion, (float)grappleClawSkyLight, (float)winchWielderSkyLight);
        int light = LightmapTextureManager.pack(blockLight, skyLight);
        float colorModifier = segmentIndex % 2 == (isLeashKnot ? 1 : 0) ? 0.7F : 1.0F;
        float red = 0.5F * colorModifier;
        float green = 0.4F * colorModifier;
        float blue = 0.3F * colorModifier;
        float x = grappleClawX * segmentProportion;
        float y = grappleClawY > 0.0F ? grappleClawY * segmentProportion * segmentProportion : grappleClawY - grappleClawY * (1.0F - segmentProportion) * (1.0F - segmentProportion);
        float z = grappleClawZ * segmentProportion;
        vertexConsumer.vertex(matrix, x - winchWielderY, y + winchWielderX, z + winchWielderZ).color(red, green, blue, 1.0F).light(light);
        vertexConsumer.vertex(matrix, x + winchWielderY, y + f - winchWielderX, z - winchWielderZ).color(red, green, blue, 1.0F).light(light);
    }
}
