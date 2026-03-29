package net.myriantics.klaxon.mechanics.wrench.overlays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.wrench.BakedWrenchOverlay;
import net.myriantics.klaxon.mechanics.wrench.SelectedFaceCalculator;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.registry.render.KlaxonTextures;
import org.jetbrains.annotations.Nullable;

public abstract sealed class AbstractWrenchInteractionOverlay permits DetachedWrenchInteractionOverlay, SelectedWrenchInteractionOverlay {

    private static final ResourceLocation TEXTURE = KlaxonTextures.decorate(KlaxonTextures.WRENCH_OVERLAY_9SLICE);

    protected BlockState stateCache = null;
    protected Direction directionCache = null;
    protected float clickedAxisValCache = 0;

    public void resetCache(WrenchActionContext.Manual manual) {
        this.directionCache = manual.getHitResult().getDirection();
        this.stateCache = manual.getTargetState();
        this.clickedAxisValCache = (float) manual.getClickPosFromCenter().get(this.directionCache.getAxis());
    }

    public Direction getDirectionCache() {
        return directionCache;
    }

    public boolean validate(WrenchActionContext.Manual manual) {
        Direction clickedDirection = manual.getHitResult().getDirection();
        BlockState newState = manual.level().getBlockState(manual.getTargetPos());
        float clickedAxisVal = (float) manual.getClickPosFromCenter().get(clickedDirection.getAxis());

        return clickedDirection.equals(this.directionCache) && newState.equals(this.stateCache) && SelectedFaceCalculator.testWithAllowance(this.clickedAxisValCache, clickedAxisVal);
    }

    public abstract @Nullable BakedWrenchOverlay getBakedOverlay();

    protected int getAlpha(float tickDelta) {
        return 0xB0;
    }

    public void tick(WrenchActionContext.Manual context) {

    }

    public void render(WrenchActionContext.Manual context, Camera camera, DeltaTracker deltaTracker, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, ProfilerFiller profilerFiller) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(TEXTURE));
        BlockPos pos = context.getTargetPos();
        Direction faceDir = context.getHitResult().getDirection();
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

        int alpha = this.getAlpha(deltaTracker.getGameTimeDeltaPartialTick(false));
        int light = LightTexture.pack(15, 15);
        BakedWrenchOverlay overlay = this.getBakedOverlay();
        if (overlay != null) {
            overlay.render(pose, consumer, alpha, light);
        }

        poseStack.popPose();
    }


}