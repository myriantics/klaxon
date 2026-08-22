package net.myriantics.klaxon.mechanics.grapple_winch;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.entity.entities.projectile.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.render.KlaxonTextures;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Collection;

public final class GrappleWinchCableRenderer {

    private static final ResourceLocation CABLE_SEGMENT_TEXTURE = KlaxonTextures.decorate(KlaxonTextures.STEEL_CABLE_SEGMENT);

    private static final float TEXTURE_U_MIN = 0f/16;
    private static final float TEXTURE_U_MAX = 16f/16;
    private static final float TEXTURE_V_MIN = 0f/16;
    private static final float TEXTURE_V_MAX = 3f/16;

    public void render(
            ClientLevel clientWorld,
            Camera camera,
            DeltaTracker renderTickCounter,
            PoseStack matrices,
            MultiBufferSource immediate,
            Collection<ClientGrappleWinchConnection> connections,
            float daylightMultiplier,
            boolean clientPlayerHasNightVision
    ) {
        VertexConsumer vertexConsumer = immediate.getBuffer(RenderType.entityCutoutNoCull(CABLE_SEGMENT_TEXTURE));
        Vec3 cameraPos = camera.getPosition();

        for (ClientGrappleWinchConnection connection : connections) {

            @Nullable Player player = connection.getPlayer();
            @Nullable GrapplingHook hook = connection.getHook();

            Entity source = player == null || player.isRemoved()
                    ? hook == null ? null : hook.klaxon$asEntity()
                    : player;

            // don't render if neither entity is present
            if (source == null || source.isRemoved()) {
                continue;
            }

            float tickDelta = renderTickCounter.getGameTimeDeltaPartialTick(clientWorld.tickRateManager().isEntityFrozen(source));

            // initialize positions
            Vec3 playerPos = player == null || player.isRemoved() ? connection.getLerpedPlayerPos(tickDelta) : player.getPosition(tickDelta);
            Vec3 clawPos = hook == null || hook.klaxon$asEntity().isRemoved() ? connection.getLerpedHookPos(tickDelta) : hook.klaxon$asEntity().getPosition(tickDelta);
            BlockPos playerBlockPos = BlockPos.containing(playerPos);
            BlockPos clawBlockPos = BlockPos.containing(clawPos);

            if (hook instanceof GrappleClawEntity grappleClaw && grappleClaw.hasHookedEntity()) {
                clawPos = clawPos.add(0, grappleClaw.getEyeHeight(grappleClaw.getPose()), 0);
            }

            // gather light values
            int originBlockLight = clientWorld.getBrightness(LightLayer.BLOCK, playerBlockPos);
            int endpointBlockLight = clientWorld.getBrightness(LightLayer.BLOCK, clawBlockPos);
            int originSkyLight = clientWorld.getBrightness(LightLayer.SKY, playerBlockPos);
            int endpointSkyLight = clientWorld.getBrightness(LightLayer.SKY, clawBlockPos);

            // block light level is overridden to 15 if on fire or glowing
            originBlockLight = player != null && (player.isOnFire() || player.isCurrentlyGlowing()) ? 15 : originBlockLight;
            endpointBlockLight = hook != null && (hook.klaxon$asEntity().isOnFire() || hook.klaxon$asEntity().isCurrentlyGlowing()) ? 15 : endpointBlockLight;

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

            Vec3 cableOriginPos = playerPos;
            Vec3 cableEndpointPos = clawPos;

            // override cable origin pos with player hand position if possible
            if (player != null && !player.isRemoved()) {
                float swingProgress = player.getAttackAnim(tickDelta);
                float handMovementOffset = Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);
                cableOriginPos = this.getHandPos(connection.getPlayer(), camera, handMovementOffset, tickDelta);
            }

            // yonk the lerped values
            double lerpedX = Mth.lerp(tickDelta, source.xOld, source.getX());
            double lerpedY = Mth.lerp(tickDelta, source.yOld, source.getY());
            double lerpedZ = Mth.lerp(tickDelta, source.zOld, source.getZ());

            matrices.pushPose();
            matrices.translate(lerpedX - cameraPos.x(), lerpedY - cameraPos.y(), lerpedZ - cameraPos.z());
            matrices.translate(cableEndpointPos.x() - lerpedX, cableEndpointPos.y() - lerpedY, cableEndpointPos.z() - lerpedZ);

            double distance = cableOriginPos.distanceTo(cableEndpointPos);
            int maxSegments = (int) distance;

            matrices.mulPose(Axis.YP.rotationDegrees(90 - KlaxonMathHelper.yawBetween(cableOriginPos, cableEndpointPos)));
            matrices.mulPose(
                    Axis.ZP.rotationDegrees(KlaxonMathHelper.pitchBetween(cableOriginPos, cableEndpointPos))
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

                matrices.pushPose();

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
                matrices.popPose();
            }

            matrices.popPose();
        }
    }

    private void renderCableSegment(
            float lengthToRender,
            VertexConsumer vertexConsumer,
            int cableOriginBlockLight,
            int cableOriginSkyLight,
            int cableEndpointBlockLight,
            int cableEndpointSkyLight,
            PoseStack matrices,
            float segmentStartPercentage
    ) {
        // do lighting calculations
        int lerpedBlockLight = Mth.lerpInt(segmentStartPercentage, cableEndpointBlockLight, cableOriginBlockLight);
        int lerpedSkyLight = Mth.lerpInt(segmentStartPercentage, cableEndpointSkyLight, cableOriginSkyLight);
        int light = LightTexture.pack(lerpedBlockLight, lerpedSkyLight);



        matrices.mulPose(Axis.XP.rotationDegrees(45));

        float uMin = TEXTURE_U_MIN;
        float uMax = TEXTURE_U_MAX * lengthToRender;
        float xTo = -16 * lengthToRender;

        // cable thickness is 3 pixels, but it must be divided by 2 for it to be centered
        float cableThickness = 3f;

        for (int i = 0; i < 2; i++) {
            matrices.mulPose(Axis.XP.rotationDegrees(90));
            PoseStack.Pose entry2 = matrices.last();

            this.vertex(entry2, vertexConsumer, 0, cableThickness / 2, 0, uMin, TEXTURE_V_MIN, 0, 0, 0, light);
            this.vertex(entry2, vertexConsumer, xTo, cableThickness / 2, 0, uMax, TEXTURE_V_MIN, 0, 0, 0, light);
            this.vertex(entry2, vertexConsumer, xTo, -cableThickness / 2, 0, uMax, TEXTURE_V_MAX, 0, 0, 0, light);
            this.vertex(entry2, vertexConsumer, 0, -cableThickness / 2, 0, uMin, TEXTURE_V_MAX, 0, 0, 0, light);
        }
    }

    public Vec3 getHandPos(Player player, Camera camera, float f, float tickDelta) {
        int handInverter = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        boolean isUsing = player.getUseItem().is(KlaxonItems.GRAPPLE_WINCH);
        boolean isSneaking = player.isCrouching();
        ItemStack itemStack = player.getMainHandItem();
        if (!(itemStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(itemStack))) {
            handInverter = -handInverter;
        }

        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double m = 960.0 / Minecraft.getInstance().options.fov().get();
            Vec3 vec3d = camera.getNearPlane().getPointOnPlane(handInverter * 0.4F, -0.5F).scale(m).yRot(f * 0.25F).xRot(-f * 0.3F);

            return player.getEyePosition(tickDelta).add(vec3d);
        } else {
            float scale = player.getScale();

            float sneakOffset = isSneaking ? -0.1375F : -0.0675F;


            Vec3 vec3d = Vec3.ZERO;

            if (isUsing) {
                float headYawRadians = Mth.lerp(tickDelta, player.yHeadRotO, player.yHeadRot) * (float) (Math.PI / 180);
                float headPitchRadians = Mth.lerp(tickDelta, player.xRotO, player.getXRot()) * (float) (Math.PI / 180);

                double sinHeadYaw = Mth.sin(headYawRadians);
                double cosHeadYaw = Mth.cos(headYawRadians);
                double sinHeadPitch = Mth.sin(headPitchRadians);
                double cosHeadPitch = Mth.cos(headPitchRadians);

                // lateral offset is 2 pixels away from head center
                // facing offset is
                double lateralOffset = handInverter * scale * 0.125;
                double facingOffset = scale * 1;
                float verticalOffset = 0.8625f;

                vec3d = new Vec3(
                        -cosHeadYaw * lateralOffset - sinHeadYaw * facingOffset * cosHeadPitch,
                        sneakOffset - verticalOffset * scale,
                        -sinHeadYaw * lateralOffset + cosHeadYaw * facingOffset
                ).multiply(
                        cosHeadPitch,
                        sinHeadPitch,
                        cosHeadPitch
                );
            } else {
                float bodyYawRadians = Mth.lerp(tickDelta, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
                double sinBodyYaw = Mth.sin(bodyYawRadians);
                double cosBodyYaw = Mth.cos(bodyYawRadians);

                double lateralOffset = handInverter * scale * 0.375;
                double facingOffset = scale * (isSneaking ? -0.0475 : 0.2875);
                float verticalOffset = 0.8625f;

                vec3d = new Vec3(
                        -cosBodyYaw * lateralOffset - sinBodyYaw * facingOffset,
                        sneakOffset - verticalOffset * scale,
                        -sinBodyYaw * lateralOffset + cosBodyYaw * facingOffset
                );
            }

            return player.getEyePosition(tickDelta).add(vec3d.subtract(0, 0.45, 0));
        }
    }

    private void vertex(
            PoseStack.Pose matrix, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, float normalX, float normalZ, float normalY, int light
    ) {
        vertexConsumer.addVertex(matrix, x, y, z)
                .setColor(CommonColors.WHITE)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(matrix, normalX, normalY, normalZ);
    }
}
