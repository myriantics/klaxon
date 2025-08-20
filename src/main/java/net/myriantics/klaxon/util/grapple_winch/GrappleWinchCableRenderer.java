package net.myriantics.klaxon.util.grapple_winch;

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

    public static void renderGrappleWinchCable(Entity source, @Nullable Camera camera, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d connectionPos, int blockLight) {
        matrices.push();
        double d = source.lerpYaw(tickDelta) * (float) (Math.PI / 180.0) + (Math.PI / 2);
        Vec3d vec3d2 = source.getLeashOffset(tickDelta);
        double e = Math.cos(d) * vec3d2.z + Math.sin(d) * vec3d2.x;
        double f = Math.sin(d) * vec3d2.z - Math.cos(d) * vec3d2.x;

        Position sourcePos = source.getPos();

        if (source instanceof PlayerEntity player && camera != null) {
            float h = player.getHandSwingProgress(tickDelta);
            float j = MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
            sourcePos = GrappleWinchConnectionManager.INSTANCE.getHandPos(player, camera, j, tickDelta);
        }

        double g = MathHelper.lerp(tickDelta, source.prevX, sourcePos.getX()) + e;
        double h = MathHelper.lerp(tickDelta, source.prevY, sourcePos.getY()) + vec3d2.y;
        double i = MathHelper.lerp(tickDelta, source.prevZ, sourcePos.getZ()) + f;
        matrices.translate(e, vec3d2.y, f);
        float j = (float)(connectionPos.x - g);
        float k = (float)(connectionPos.y - h);
        float l = (float)(connectionPos.z - i);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float n = MathHelper.inverseSqrt(j * j + l * l) * 0.025F / 2.0F;
        float o = l * n;
        float p = j * n;
        BlockPos blockPos = BlockPos.ofFloored(source.getCameraPosVec(tickDelta));
        BlockPos blockPos2 = BlockPos.ofFloored(connectionPos);
        int q = blockLight;
        int r = q;
        int s = source.getWorld().getLightLevel(LightType.SKY, blockPos);
        int t = source.getWorld().getLightLevel(LightType.SKY, blockPos2);

        for (int u = 0; u <= 24; u++) {
            renderCableSegment(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, 0.025F, o, p, u, false);
        }

        for (int u = 24; u >= 0; u--) {
            renderCableSegment(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, 0.0F, o, p, u, true);
        }

        matrices.pop();
    }

    private static void renderCableSegment(
            VertexConsumer vertexConsumer,
            Matrix4f matrix,
            float leashedEntityX,
            float leashedEntityY,
            float leashedEntityZ,
            int leashedEntityBlockLight,
            int leashHolderBlockLight,
            int leashedEntitySkyLight,
            int leashHolderSkyLight,
            float f,
            float g,
            float h,
            float i,
            int segmentIndex,
            boolean isLeashKnot
    ) {
        float j = segmentIndex / 24.0F;
        int k = (int)MathHelper.lerp(j, (float)leashedEntityBlockLight, (float)leashHolderBlockLight);
        int l = (int)MathHelper.lerp(j, (float)leashedEntitySkyLight, (float)leashHolderSkyLight);
        int m = LightmapTextureManager.pack(k, l);
        float n = segmentIndex % 2 == (isLeashKnot ? 1 : 0) ? 0.7F : 1.0F;
        float o = 0.5F * n;
        float p = 0.4F * n;
        float q = 0.3F * n;
        float r = leashedEntityX * j;
        float s = leashedEntityY > 0.0F ? leashedEntityY * j * j : leashedEntityY - leashedEntityY * (1.0F - j) * (1.0F - j);
        float t = leashedEntityZ * j;
        vertexConsumer.vertex(matrix, r - h, s + g, t + i).color(o, p, q, 1.0F).light(m);
        vertexConsumer.vertex(matrix, r + h, s + f - g, t - i).color(o, p, q, 1.0F).light(m);
    }
}
