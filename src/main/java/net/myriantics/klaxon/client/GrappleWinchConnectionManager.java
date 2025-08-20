package net.myriantics.klaxon.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.util.grapple_winch.GrappleWinchCableRenderer;
import net.myriantics.klaxon.util.grapple_winch.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum GrappleWinchConnectionManager {
    INSTANCE;

    private ArrayList<GrappleWinchConnection> activeConnections = new ArrayList<>();

    public void addConnection(@Nullable AbstractClientPlayerEntity player, @Nullable GrappleClawEntity grappleClaw, Vec3d playerPos, Vec3d clawPos) {
        activeConnections.add(
                new GrappleWinchConnection(
                        player,
                        grappleClaw,
                        playerPos,
                        clawPos,
                        playerPos.distanceTo(clawPos)
                )
        );
    }

    public void discardConnection(@Nullable AbstractClientPlayerEntity player, @Nullable GrappleClawEntity grappleClaw) {
        if (player != null) {
            activeConnections.removeIf(connection -> player.equals(connection.player));
        } else if (grappleClaw != null) {
            activeConnections.removeIf(connection -> grappleClaw.equals(connection.grappleClaw));
        }
    }

    public void renderCables(
            ClientWorld clientWorld,
            Camera camera,
            RenderTickCounter renderTickCounter,
            MatrixStack matrices,
            VertexConsumerProvider immediate
    ) {
        Vec3d cameraPos = camera.getPos();

        for (GrappleWinchConnection connection : activeConnections) {
            Entity source = connection.player == null ? connection.grappleClaw : connection.player;

            // bonk the connection if both entities deload
            if (source == null) {
                activeConnections.remove(connection);
                return;
            }

            Vec3d sourcePos = source.getPos();
            Vec3d opposingPos = connection.getOpposingPos(source);

            float tickDelta = renderTickCounter.getTickDelta(clientWorld.getTickManager().shouldSkipTick(source));
            int blockLight = source.isOnFire() ? 15 : clientWorld.getLightLevel(LightType.BLOCK, source.getBlockPos());

            double d = MathHelper.lerp(tickDelta, source.lastRenderX, sourcePos.getX());
            double e = MathHelper.lerp(tickDelta, source.lastRenderY, sourcePos.getY());
            double f = MathHelper.lerp(tickDelta, source.lastRenderZ, sourcePos.getZ());

            matrices.push();
            matrices.translate(d - cameraPos.getX(), e - cameraPos.getY(), f - cameraPos.getZ());

            GrappleWinchCableRenderer.renderGrappleWinchCable(source, camera, tickDelta, matrices, immediate, opposingPos, blockLight);

            matrices.pop();
        }
    }

    public Vec3d getHandPos(PlayerEntity player, Camera camera, float f, float tickDelta) {
        int i = player.getMainArm() == Arm.RIGHT ? 1 : -1;
        ItemStack itemStack = player.getMainHandStack();
        if (!itemStack.isOf(KlaxonItems.GRAPPLE_WINCH)) {
            i = -i;
        }

        if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player) {
            double m = 960.0 / MinecraftClient.getInstance().options.getFov().getValue();
            Vec3d vec3d = camera.getProjection().getPosition(i * 0.525F, -0.1F).multiply(m).rotateY(f * 0.5F).rotateX(-f * 0.7F);
            return player.getCameraPosVec(tickDelta).add(vec3d);
        } else {
            float g = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * (float) (Math.PI / 180.0);
            double d = MathHelper.sin(g);
            double e = MathHelper.cos(g);
            float h = player.getScale();
            double j = i * 0.35 * h;
            double k = 0.8 * h;
            float l = player.isInSneakingPose() ? -0.1875F : 0.0F;
            return player.getCameraPosVec(tickDelta).add(-e * j - d * k, l - 0.45 * h, -d * j + e * k);
        }
    }

    private record GrappleWinchConnection(
            AbstractClientPlayerEntity player,
            GrappleClawEntity grappleClaw,
            Vec3d playerPos,
            Vec3d clawPos,
            double spentRange
    ) {
        public Vec3d getOpposingPos(@NotNull Entity origin) {
            if (origin instanceof AbstractClientPlayerEntity) {
                return clawPos();
            } else {
                return playerPos();
            }
        }
    }
}
