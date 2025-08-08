package net.myriantics.klaxon.mixin.grapple_winch;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.util.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(
            method = "onClientCommand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSneaking(Z)V", ordinal = 0)
    )
    public void klaxon$resetGrappleWinchTargetPosition(ClientCommandC2SPacket packet, CallbackInfo ci) {
        if (player instanceof PlayerEntityGrappleAccess access && !player.isOnGround()) {
            GrappleClawEntity grappleClaw = access.klaxon$getGrappleClaw();
            if (grappleClaw != null) grappleClaw.resetTargetRangeSquared();
        }
    }
}
