package net.myriantics.klaxon.mechanics.wrench.overlays;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.myriantics.klaxon.mechanics.wrench.BakedWrenchOverlay;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;

public final class DetachedWrenchInteractionOverlay extends AbstractWrenchInteractionOverlay {
    private int ticks;
    private final int maxTicks;
    private final WrenchActionContext.Manual context;
    private final BakedWrenchOverlay overlay;

    public DetachedWrenchInteractionOverlay(WrenchActionContext.Manual manual, BakedWrenchOverlay overlay, int maxTicks) {
        this.context = manual;
        this.maxTicks = maxTicks;
        this.ticks = maxTicks;
        this.overlay = overlay;
    }

    @Override
    public void tick(WrenchActionContext.Manual context) {
        super.tick(context);
        this.ticks--;
    }

    @Override
    protected int getAlpha(float tickDelta) {
        return (int) (super.getAlpha(tickDelta) * (Mth.lerp(tickDelta, this.ticks + 1, this.ticks) / this.maxTicks));
    }

    @Override
    public boolean validate(WrenchActionContext.Manual manual) {
        return super.validate(this.context) && this.ticks > 0;
    }

    @Override
    public BakedWrenchOverlay getBakedOverlay() {
        return this.overlay;
    }

    @Override
    public void render(WrenchActionContext.Manual context, Camera camera, DeltaTracker deltaTracker, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, ProfilerFiller profilerFiller) {
        super.render(this.context, camera, deltaTracker, poseStack, bufferSource, profilerFiller);
    }
}
