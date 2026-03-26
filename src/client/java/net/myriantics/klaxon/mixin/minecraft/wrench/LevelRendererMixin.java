package net.myriantics.klaxon.mixin.minecraft.wrench;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.WrenchInteractionOverlayRenderer;
import net.myriantics.klaxon.mechanics.wrench.WrenchUtil;
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

    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private final WrenchInteractionOverlayRenderer klaxon$wrenchOverlayRenderer = new WrenchInteractionOverlayRenderer();

    @Inject(
            method = "renderLevel",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/HitResult$Type;BLOCK:Lnet/minecraft/world/phys/HitResult$Type;", opcode = Opcodes.GETSTATIC)
            ),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;hitResult:Lnet/minecraft/world/phys/HitResult;", opcode = Opcodes.GETFIELD)
    )
    private void klaxon$renderWrenchOverlay(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local ProfilerFiller profilerFiller, @Local PoseStack poseStack, @Local MultiBufferSource.BufferSource bufferSource) {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        LocalPlayer player = this.minecraft.player;
        if (hitResult instanceof BlockHitResult blockHitResult && this.level != null && player != null) {
            @Nullable InteractionHand wrenchHand = WrenchUtil.selectWrenchHand(player);

            if (wrenchHand != null) {
                ItemStack wrenchStack = player.getItemInHand(wrenchHand);
                BlockPos pos = blockHitResult.getBlockPos();
                BlockState state = this.level.getBlockState(pos);

                WrenchActionContext.Manual manual = new WrenchActionContext.Manual(this.level, state, pos, wrenchStack, player, blockHitResult, wrenchHand);

                if (!state.isAir() && this.klaxon$wrenchOverlayRenderer.shouldRender()) {
                    profilerFiller.popPush("klaxon:wrench_overlay");
                    this.klaxon$wrenchOverlayRenderer.render(this.level, manual, camera, deltaTracker, poseStack, bufferSource, profilerFiller);
                }
            }
        }
    }


}
