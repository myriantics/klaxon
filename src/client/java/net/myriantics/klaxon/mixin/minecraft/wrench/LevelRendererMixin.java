package net.myriantics.klaxon.mixin.minecraft.wrench;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.myriantics.klaxon.mechanics.wrench.WrenchInteractionOverlayManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    private @Nullable ClientLevel level;

    @Inject(
            method = "renderLevel",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/HitResult$Type;BLOCK:Lnet/minecraft/world/phys/HitResult$Type;", opcode = Opcodes.GETSTATIC)
            ),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;hitResult:Lnet/minecraft/world/phys/HitResult;", opcode = Opcodes.GETFIELD)
    )
    private void klaxon$renderWrenchOverlay(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local ProfilerFiller profilerFiller, @Local PoseStack poseStack, @Local MultiBufferSource.BufferSource bufferSource) {
        if (this.level instanceof WrenchInteractionOverlayManager.Access access && access.klaxon$get().shouldRender()) {
            profilerFiller.popPush("klaxon:wrench_overlay");
            access.klaxon$get().render(camera, deltaTracker, poseStack, bufferSource, profilerFiller);
        }
    }
}
