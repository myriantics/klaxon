package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.myriantics.klaxon.client.GrappleWinchClientConnectionManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    private @Nullable ClientWorld world;

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;drawCurrentLayer()V", ordinal = 0)
    )
    private void klaxon$renderGrappleWinchCables(
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f matrix4f,
            Matrix4f matrix4f2,
            CallbackInfo ci,
            @Local MatrixStack matrixStack,
            @Local VertexConsumerProvider.Immediate immediate
            ) {
        if (world != null) {
            world.getProfiler().push("grapple_winch_cables");
            GrappleWinchClientConnectionManager.INSTANCE.renderGrappleWinchCable(world, camera, tickCounter, matrixStack, immediate);
            world.getProfiler().pop();
        }
    }
}
