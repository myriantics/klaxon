package net.myriantics.klaxon.mechanics.wrench;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.myriantics.klaxon.mechanics.wrench.overlays.DetachedWrenchInteractionOverlay;
import net.myriantics.klaxon.mechanics.wrench.overlays.SelectedWrenchInteractionOverlay;
import net.myriantics.klaxon.util.BlockFaceRegion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public final class WrenchInteractionOverlayManager {
    private final SelectedWrenchInteractionOverlay selected = new SelectedWrenchInteractionOverlay();
    private final HashMap<BlockPos, DetachedWrenchInteractionOverlay> detachedOverlays = new HashMap<>();
    private final Minecraft minecraft = Minecraft.getInstance();
    private WrenchActionContext.Manual context = null;

    public WrenchInteractionOverlayManager() {
    }

    public boolean shouldRender() {
        if (this.context == null) {
            return false;
        }
        Direction direction = this.context.getHitResult().getDirection();
        return this.context.getPlayer().mayUseItemAt(context.getTargetPos().relative(direction, -1), direction, context.getWrenchStack());
    }

    public void tick() {
        this.refreshContext();
        if (this.context != null) {
            this.selected.tick(this.context);
            if (this.context.level().tickRateManager().runsNormally()) {
                ArrayList<BlockPos> removals = new ArrayList<>();
                for (BlockPos pos : this.detachedOverlays.keySet()) {
                    DetachedWrenchInteractionOverlay overlay = this.detachedOverlays.get(pos);
                    if (overlay.validate(this.context)) {
                        overlay.tick(this.context);
                    } else {
                        removals.add(pos);
                    }
                }
                for (BlockPos removed : removals) {
                    this.detachedOverlays.remove(removed);
                }
            }
        }
    }

    public void spawnDetachedInteractionOverlay(int ticksToDespawn) {
        BakedWrenchOverlay selected = this.selected.getBakedOverlay();


        if (this.context != null) {
            if (selected == null) {
                Direction dir = this.context.clickedDirection();
                Level level = this.context.level();
                BlockState targetState = this.context.getTargetState();
                BlockPos targetPos = this.context.getTargetPos();

                SelectedFaceCalculator calculator = new SelectedFaceCalculator(dir, this.context.getClickPosFromCorner().toVector3f());
                targetState.getShape(level, targetPos).forAllEdges(calculator::tryAdd);
                selected = BakedWrenchOverlay.of(calculator.get(), this.context);
            }

            DetachedWrenchInteractionOverlay overlay = new DetachedWrenchInteractionOverlay(this.context, selected, ticksToDespawn);
            overlay.resetCache(this.context);
            this.detachedOverlays.put(this.context.getTargetPos(), overlay);
        }
    }

    private void refreshContext() {
        HitResult hitResult = this.minecraft.hitResult;
        LocalPlayer player = this.minecraft.player;
        ClientLevel level = this.minecraft.level;
        if (hitResult instanceof BlockHitResult blockHitResult && level != null && player != null) {
            @Nullable InteractionHand wrenchHand = WrenchUtil.selectWrenchHand(player);

            if (wrenchHand != null) {
                ItemStack wrenchStack = player.getItemInHand(wrenchHand);
                BlockPos pos = blockHitResult.getBlockPos();
                BlockState state = level.getBlockState(pos);

                this.context = new WrenchActionContext.Manual(level, state, pos, wrenchStack, player, blockHitResult, wrenchHand);
                return;
            }
        }
        this.context = null;
    }

    public void render(Camera camera, DeltaTracker deltaTracker, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, ProfilerFiller profilerFiller) {
        profilerFiller.push("selected_overlay");
        this.selected.render(context, camera, deltaTracker, poseStack, bufferSource, profilerFiller);
        profilerFiller.popPush("detached_overlays");
        for (DetachedWrenchInteractionOverlay overlay : this.detachedOverlays.values()) {
            overlay.render(this.context, camera, deltaTracker, poseStack, bufferSource, profilerFiller);
        }
        profilerFiller.pop();
    }

    public interface Access {
        WrenchInteractionOverlayManager klaxon$get();
    }
}
