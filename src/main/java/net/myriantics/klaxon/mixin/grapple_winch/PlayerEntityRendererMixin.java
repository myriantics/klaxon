package net.myriantics.klaxon.mixin.grapple_winch;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.util.grapple_winch.GrappleWinchCableRenderer;
import net.myriantics.klaxon.util.grapple_winch.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD")
    )
    private void klaxon$renderGrappleWinchCable(AbstractClientPlayerEntity abstractClientPlayer, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (abstractClientPlayer instanceof PlayerEntityGrappleAccess access) {
            if (access.klaxon$hasActiveConnection()) {
                GrappleClawEntity grappleClaw = access.klaxon$getGrappleClaw();
                Vec3d connectionPos = grappleClaw != null ? grappleClaw.getPos() : access.klaxon$getWinchFallbackData().winchConnectedPos();

                int blockLight = getBlockLight((T) abstractClientPlayer, abstractClientPlayer.getBlockPos());
                int endpointBlockLight = getBlockLight((T) abstractClientPlayer, BlockPos.ofFloored(connectionPos));

                GrappleWinchCableRenderer.renderGrappleWinchCable(abstractClientPlayer, tickDelta, matrixStack, vertexConsumerProvider, connectionPos, blockLight, endpointBlockLight);
            }
        }
    }
}
