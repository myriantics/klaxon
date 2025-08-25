package net.myriantics.klaxon.client;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public enum GrappleWinchConnectionManager {
    INSTANCE;

    private final Map<Integer, GrappleWinchConnection> playerIdToActiveConnections = new HashMap<>();

    public void addConnection(
            int playerId,
            int grappleClawId,
            @Nullable AbstractClientPlayerEntity player,
            @Nullable GrappleClawEntity grappleClaw,
            Vec3d playerPos,
            Vec3d clawPos
    ) {
        if (player == null && grappleClaw == null) {
            return;
        }

        GrappleWinchConnection connection = playerIdToActiveConnections.get(playerId);

        if (connection == null) {
            playerIdToActiveConnections.put(playerId,
                    new GrappleWinchConnection(
                            grappleClawId,
                            Optional.ofNullable(player),
                            Optional.ofNullable(grappleClaw),
                            playerPos,
                            clawPos
                    )
            );
        } else {
            playerIdToActiveConnections.put(playerId,
                    new GrappleWinchConnection(
                            grappleClawId,
                            connection.player,
                            connection.grappleClaw().isPresent() ? connection.grappleClaw() : Optional.ofNullable(grappleClaw),
                            playerPos,
                            clawPos
                    )
            );
        }
    }

    public void discardConnection(int playerId) {
        playerIdToActiveConnections.remove(playerId);
    }

    public void clientTick(MinecraftClient client) {
        if (client.world instanceof ClientWorld clientWorld) {
            // we must copy the key set here so editing the map doesn't fuck shit up
            for (int playerId : List.copyOf(playerIdToActiveConnections.keySet())) {
                GrappleWinchConnection connection = playerIdToActiveConnections.get(playerId);
                int grappleClawId = connection.grappleClawId();

                Entity potentialPlayer = clientWorld.getEntityById(playerId);
                Entity potentialGrappleClaw = clientWorld.getEntityById(grappleClawId);

                if (potentialPlayer instanceof PlayerEntity || potentialGrappleClaw instanceof GrappleClawEntity) {
                    playerIdToActiveConnections.put(playerId, new GrappleWinchConnection(
                            grappleClawId,
                            Optional.ofNullable(potentialPlayer instanceof PlayerEntity player ? player : null),
                            Optional.ofNullable(potentialGrappleClaw instanceof GrappleClawEntity claw ? claw : null),
                            potentialPlayer instanceof PlayerEntity ? potentialPlayer.getPos() : connection.playerPos(),
                            potentialGrappleClaw instanceof GrappleClawEntity ? potentialGrappleClaw.getPos() : connection.clawPos()
                    ));
                } else {
                    // remove the connection if neither player nor claw are loaded
                    playerIdToActiveConnections.remove(playerId);
                }
            }
        }
    }

    public void renderGrappleWinchCable(
            ClientWorld clientWorld,
            Camera camera,
            RenderTickCounter renderTickCounter,
            MatrixStack matrices,
            VertexConsumerProvider immediate
    ) {
        Vec3d cameraPos = camera.getPos();

        for (int playerId : playerIdToActiveConnections.keySet()) {
            GrappleWinchConnection connection = playerIdToActiveConnections.get(playerId);
            Entity source = connection.player().isPresent() ? connection.player().get() : connection.grappleClaw().orElse(null);
            if (source == null) {
                playerIdToActiveConnections.remove(playerId);
                return;
            }

            float tickDelta = renderTickCounter.getTickDelta(clientWorld.getTickManager().shouldSkipTick(source));
            int blockLight = source.isOnFire() ? 15 : clientWorld.getLightLevel(LightType.BLOCK, source.getBlockPos());

            Vec3d sourcePos = source.getPos();
            Vec3d handPos = sourcePos;

            if (connection.player().orElse(null) instanceof PlayerEntity player) {
                float swingProgress = player.getHandSwingProgress(tickDelta);
                float handMovementOffset = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
                handPos = GrappleWinchConnectionManager.INSTANCE.getHandPos(connection.player().get(), camera, handMovementOffset, tickDelta);
            }

            double lerpedX = MathHelper.lerp(tickDelta, source.lastRenderX, sourcePos.getX());
            double lerpedY = MathHelper.lerp(tickDelta, source.lastRenderY, sourcePos.getY());
            double lerpedZ = MathHelper.lerp(tickDelta, source.lastRenderZ, sourcePos.getZ());

            matrices.push();
            matrices.translate(lerpedX - cameraPos.getX(), lerpedY - cameraPos.getY(), lerpedZ - cameraPos.getZ());
            matrices.translate(handPos.getX() - lerpedX, handPos.getY() - lerpedY, handPos.getZ() - lerpedZ);

            VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getLines());
            Matrix4f entry1 = matrices.peek().getPositionMatrix();
            WorldRenderer.drawBox(matrices, vertexConsumer, 0, 0, 0, 1, 1, 1, 1f, 0f, 1f, 1.0f);

            matrices.pop();
        }
    }

    private void renderCableSegment() {

    }

    public Vec3d getHandPos(PlayerEntity player, Camera camera, float f, float tickDelta) {
        int handInverter = player.getMainArm() == Arm.RIGHT ? 1 : -1;
        boolean isUsing = player.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH);
        ItemStack itemStack = player.getMainHandStack();
        if (!itemStack.isOf(KlaxonItems.GRAPPLE_WINCH)) {
            handInverter = -handInverter;
        }

        if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player) {
            double m = 960.0 / MinecraftClient.getInstance().options.getFov().getValue();
            Vec3d vec3d = camera.getProjection().getPosition(handInverter * 0.4F, -0.5F).multiply(m).rotateY(f * 0.25F).rotateX(-f * 0.3F);

            return player.getCameraPosVec(tickDelta).add(vec3d);
        } else {
            float scale = player.getScale();
            double lateralOffset = handInverter * scale;
            double facingOffset = scale;
            float sneakOffset = player.isInSneakingPose() ? -0.1375F : -0.0675F;
            float verticalOffset = 0.8625f;

            Vec3d vec3d = Vec3d.ZERO;

            if (isUsing) {
                float headYawRadians = MathHelper.lerp(tickDelta, player.prevHeadYaw, player.headYaw) * (float) (Math.PI / 180);
                float headPitchRadians = MathHelper.lerp(tickDelta, player.prevPitch, player.getPitch()) * (float) (Math.PI / 180);

                double sinHeadYaw = MathHelper.sin(headYawRadians);
                double cosHeadYaw = MathHelper.cos(headYawRadians);

                vec3d = new Vec3d(
                        vec3d.getX(), // -cosYaw * usingLateralOffset - sinYaw * usingFacingOffset,
                        vec3d.getY() * MathHelper.sin(headPitchRadians),
                        vec3d.getZ()// -sinYaw * usingLateralOffset - cosYaw * usingFacingOffset
                );
            } else {
                float bodyYawRadians = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * (float) (Math.PI / 180.0);
                double sinBodyYaw = MathHelper.sin(bodyYawRadians);
                double cosBodyYaw = MathHelper.cos(bodyYawRadians);

                lateralOffset *= 0.375;
                facingOffset *= (player.isInSneakingPose() ? -0.0475 : 0.2875);


                        vec3d = new Vec3d(
                        -cosBodyYaw * lateralOffset - sinBodyYaw * facingOffset,
                        sneakOffset - verticalOffset * scale,
                        -sinBodyYaw * lateralOffset + cosBodyYaw * facingOffset
                );
            }

            return player.getCameraPosVec(tickDelta).add(vec3d.subtract(0, 0.45, 0));
        }
    }

    private record GrappleWinchConnection(
            int grappleClawId,
            Optional<PlayerEntity> player,
            Optional<GrappleClawEntity> grappleClaw,
            Vec3d playerPos,
            Vec3d clawPos
    ) {
        public Vec3d getOpposingPos(@NotNull Entity origin) {
            if (origin instanceof PlayerEntity) {
                return grappleClaw.isPresent() ? grappleClaw.get().getPos() : clawPos;
            } else {
                return player.isPresent() ? player.get().getPos() : playerPos();
            }
        }
    }
}
