package net.myriantics.klaxon.mechanics.grapple_winch;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.render.KlaxonTextures;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class GrappleWinchCableRenderer {

    private static final Identifier CABLE_SEGMENT_TEXTURE = KlaxonTextures.decorate(KlaxonTextures.STEEL_CABLE_SEGMENT);

    private static final float TEXTURE_U_MIN = 0f/16;
    private static final float TEXTURE_U_MAX = 16f/16;
    private static final float TEXTURE_V_MIN = 0f/16;
    private static final float TEXTURE_V_MAX = 3f/16;

    public void render(
            ClientWorld clientWorld,
            Camera camera,
            RenderTickCounter renderTickCounter,
            MatrixStack matrices,
            VertexConsumerProvider immediate,
            Collection<ClientGrappleWinchConnection> connections,
            float daylightMultiplier,
            boolean clientPlayerHasNightVision
    ) {
        VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getEntityCutoutNoCull(CABLE_SEGMENT_TEXTURE));
        Vec3d cameraPos = camera.getPos();

        for (ClientGrappleWinchConnection connection : connections) {

            @Nullable PlayerEntity player = connection.getPlayer();
            @Nullable GrapplingHook hook = connection.getHook();

            Entity source = player == null || player.isRemoved()
                    ? hook == null ? null : hook.klaxon$asEntity()
                    : player;

            // don't render if neither entity is present
            if (source == null || source.isRemoved()) {
                continue;
            }

            float tickDelta = renderTickCounter.getTickDelta(clientWorld.getTickManager().shouldSkipTick(source));

            // initialize positions
            Vec3d playerPos = player == null || player.isRemoved() ? connection.getLerpedPlayerPos(tickDelta) : player.getLerpedPos(tickDelta);
            Vec3d clawPos = hook == null || hook.klaxon$asEntity().isRemoved() ? connection.getLerpedHookPos(tickDelta) : hook.klaxon$asEntity().getLerpedPos(tickDelta);
            BlockPos playerBlockPos = BlockPos.ofFloored(playerPos);
            BlockPos clawBlockPos = BlockPos.ofFloored(clawPos);

            if (hook instanceof GrappleClawEntity grappleClaw && grappleClaw.hasHookedEntity()) {
                clawPos = clawPos.add(0, grappleClaw.getEyeHeight(grappleClaw.getPose()), 0);
            }

            // gather light values
            int originBlockLight = clientWorld.getLightLevel(LightType.BLOCK, playerBlockPos);
            int endpointBlockLight = clientWorld.getLightLevel(LightType.BLOCK, clawBlockPos);
            int originSkyLight = clientWorld.getLightLevel(LightType.SKY, playerBlockPos);
            int endpointSkyLight = clientWorld.getLightLevel(LightType.SKY, clawBlockPos);

            // block light level is overridden to 15 if on fire or glowing
            originBlockLight = player != null && (player.isOnFire() || player.isGlowing()) ? 15 : originBlockLight;
            endpointBlockLight = hook != null && (hook.klaxon$asEntity().isOnFire() || hook.klaxon$asEntity().isGlowing()) ? 15 : endpointBlockLight;

            // multiply skylight by daytime multiplier (computed in clientTick())
            // this is needed because the skylight is always 15 when you're in the open regardless of whether it's night
            originSkyLight = (int) (originSkyLight * daylightMultiplier);
            endpointSkyLight = (int) (endpointSkyLight * daylightMultiplier);

            // all cables have decently high brightness if the camera player has night vision
            if (clientPlayerHasNightVision) {
                originBlockLight = Math.max(originBlockLight, 13);
                endpointBlockLight = Math.max(endpointBlockLight, 13);
                originSkyLight = Math.max(originSkyLight, 13);
                endpointSkyLight = Math.max(endpointSkyLight, 13);
            }

            Vec3d cableOriginPos = playerPos;
            Vec3d cableEndpointPos = clawPos;

            // override cable origin pos with player hand position if possible
            if (player != null && !player.isRemoved()) {
                float swingProgress = player.getHandSwingProgress(tickDelta);
                float handMovementOffset = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
                cableOriginPos = this.getHandPos(connection.getPlayer(), camera, handMovementOffset, tickDelta);
            }

            // yonk the lerped values
            double lerpedX = MathHelper.lerp(tickDelta, source.lastRenderX, source.getX());
            double lerpedY = MathHelper.lerp(tickDelta, source.lastRenderY, source.getY());
            double lerpedZ = MathHelper.lerp(tickDelta, source.lastRenderZ, source.getZ());

            matrices.push();
            matrices.translate(lerpedX - cameraPos.getX(), lerpedY - cameraPos.getY(), lerpedZ - cameraPos.getZ());
            matrices.translate(cableEndpointPos.getX() - lerpedX, cableEndpointPos.getY() - lerpedY, cableEndpointPos.getZ() - lerpedZ);

            double distance = cableOriginPos.distanceTo(cableEndpointPos);
            int maxSegments = (int) distance;

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 - KlaxonMathHelper.yawBetween(cableOriginPos, cableEndpointPos)));
            matrices.multiply(
                    RotationAxis.POSITIVE_Z.rotationDegrees(KlaxonMathHelper.pitchBetween(cableOriginPos, cableEndpointPos))
            );
            matrices.scale(1f/16, 1f/16, 1f/16);

            // cable segments are 2 per block of distance
            for (int segmentIndex = 0; segmentIndex <= maxSegments; segmentIndex++) {
                if (segmentIndex != 0) {
                    matrices.translate(
                            -16,
                            0,
                            0
                    );
                }

                matrices.push();

                renderCableSegment(
                        // makes it seem like the cable is actually streaming out of the grapple winch
                        segmentIndex == maxSegments
                                ? (float) (distance - (int) distance)
                                : 1,
                        vertexConsumer,
                        originBlockLight,
                        originSkyLight,
                        endpointBlockLight,
                        endpointSkyLight,
                        matrices,
                        (float) segmentIndex / maxSegments
                );
                matrices.pop();
            }

            matrices.pop();
        }
    }

    private void renderCableSegment(
            float lengthToRender,
            VertexConsumer vertexConsumer,
            int cableOriginBlockLight,
            int cableOriginSkyLight,
            int cableEndpointBlockLight,
            int cableEndpointSkyLight,
            MatrixStack matrices,
            float segmentStartPercentage
    ) {
        // do lighting calculations
        int lerpedBlockLight = MathHelper.lerp(segmentStartPercentage, cableEndpointBlockLight, cableOriginBlockLight);
        int lerpedSkyLight = MathHelper.lerp(segmentStartPercentage, cableEndpointSkyLight, cableOriginSkyLight);
        int light = LightmapTextureManager.pack(lerpedBlockLight, lerpedSkyLight);



        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45));

        float uMin = TEXTURE_U_MIN;
        float uMax = TEXTURE_U_MAX * lengthToRender;
        float xTo = -16 * lengthToRender;

        // cable thickness is 3 pixels, but it must be divided by 2 for it to be centered
        float cableThickness = 3f;

        for (int i = 0; i < 2; i++) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            MatrixStack.Entry entry2 = matrices.peek();

            this.vertex(entry2, vertexConsumer, 0, cableThickness / 2, 0, uMin, TEXTURE_V_MIN, 0, 0, 0, light);
            this.vertex(entry2, vertexConsumer, xTo, cableThickness / 2, 0, uMax, TEXTURE_V_MIN, 0, 0, 0, light);
            this.vertex(entry2, vertexConsumer, xTo, -cableThickness / 2, 0, uMax, TEXTURE_V_MAX, 0, 0, 0, light);
            this.vertex(entry2, vertexConsumer, 0, -cableThickness / 2, 0, uMin, TEXTURE_V_MAX, 0, 0, 0, light);
        }
    }

    public Vec3d getHandPos(PlayerEntity player, Camera camera, float f, float tickDelta) {
        int handInverter = player.getMainArm() == Arm.RIGHT ? 1 : -1;
        boolean isUsing = player.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH);
        boolean isSneaking = player.isInSneakingPose();
        ItemStack itemStack = player.getMainHandStack();
        if (!(itemStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(itemStack))) {
            handInverter = -handInverter;
        }

        if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player) {
            double m = 960.0 / MinecraftClient.getInstance().options.getFov().getValue();
            Vec3d vec3d = camera.getProjection().getPosition(handInverter * 0.4F, -0.5F).multiply(m).rotateY(f * 0.25F).rotateX(-f * 0.3F);

            return player.getCameraPosVec(tickDelta).add(vec3d);
        } else {
            float scale = player.getScale();

            float sneakOffset = isSneaking ? -0.1375F : -0.0675F;


            Vec3d vec3d = Vec3d.ZERO;

            if (isUsing) {
                float headYawRadians = MathHelper.lerp(tickDelta, player.prevHeadYaw, player.headYaw) * (float) (Math.PI / 180);
                float headPitchRadians = MathHelper.lerp(tickDelta, player.prevPitch, player.getPitch()) * (float) (Math.PI / 180);

                double sinHeadYaw = MathHelper.sin(headYawRadians);
                double cosHeadYaw = MathHelper.cos(headYawRadians);
                double sinHeadPitch = MathHelper.sin(headPitchRadians);
                double cosHeadPitch = MathHelper.cos(headPitchRadians);

                // lateral offset is 2 pixels away from head center
                // facing offset is
                double lateralOffset = handInverter * scale * 0.125;
                double facingOffset = scale * 1;
                float verticalOffset = 0.8625f;

                vec3d = new Vec3d(
                        -cosHeadYaw * lateralOffset - sinHeadYaw * facingOffset * cosHeadPitch,
                        sneakOffset - verticalOffset * scale,
                        -sinHeadYaw * lateralOffset + cosHeadYaw * facingOffset
                ).multiply(
                        cosHeadPitch,
                        sinHeadPitch,
                        cosHeadPitch
                );
            } else {
                float bodyYawRadians = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * (float) (Math.PI / 180.0);
                double sinBodyYaw = MathHelper.sin(bodyYawRadians);
                double cosBodyYaw = MathHelper.cos(bodyYawRadians);

                double lateralOffset = handInverter * scale * 0.375;
                double facingOffset = scale * (isSneaking ? -0.0475 : 0.2875);
                float verticalOffset = 0.8625f;

                vec3d = new Vec3d(
                        -cosBodyYaw * lateralOffset - sinBodyYaw * facingOffset,
                        sneakOffset - verticalOffset * scale,
                        -sinBodyYaw * lateralOffset + cosBodyYaw * facingOffset
                );
            }

            return player.getCameraPosVec(tickDelta).add(vec3d.subtract(0, 0.45, 0));
        }
    }

    private void vertex(
            MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, float normalX, float normalZ, float normalY, int light
    ) {
        vertexConsumer.vertex(matrix, x, y, z)
                .color(Colors.WHITE)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(matrix, normalX, normalY, normalZ);
    }
}
