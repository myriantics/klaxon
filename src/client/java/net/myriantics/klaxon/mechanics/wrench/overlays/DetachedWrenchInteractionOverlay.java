package net.myriantics.klaxon.mechanics.wrench.overlays;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.util.BlockFaceRegion;

import java.awt.*;

public final class DetachedWrenchInteractionOverlay extends AbstractWrenchInteractionOverlay {
    private int ticks;
    private final int maxTicks;
    private final WrenchActionContext.Manual context;
    private final BlockFaceRegion region;

    public DetachedWrenchInteractionOverlay(WrenchActionContext.Manual manual, BlockFaceRegion region, int maxTicks) {
        this.context = manual;
        this.maxTicks = maxTicks;
        this.ticks = maxTicks;
        this.region = region;
    }

    @Override
    public void tick(WrenchActionContext.Manual context) {
        super.tick(context);
        this.ticks--;
    }

    @Override
    protected int getColor(float tickDelta) {
        int color = super.getColor(tickDelta);

        int alpha = color & (0xFF << 24);
        color ^= alpha;
        alpha >>= 24;
        alpha &= 0xFF;
        alpha = (int) (alpha * Mth.lerp(tickDelta, this.ticks + 1, this.ticks) / this.maxTicks);
        alpha &= 0xFF;
        return color | (alpha << 24);
    }

    @Override
    public boolean validate(WrenchActionContext.Manual manual) {
        return super.validate(this.context) && this.ticks > 0;
    }

    @Override
    public BlockFaceRegion getRegion() {
        return this.region;
    }

    @Override
    public void render(WrenchActionContext.Manual context, Camera camera, DeltaTracker deltaTracker, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, ProfilerFiller profilerFiller) {
        super.render(this.context, camera, deltaTracker, poseStack, bufferSource, profilerFiller);
    }
}
