package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import org.jetbrains.annotations.Nullable;
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
        if (!player.isOnGround()) {
            ServerGrappleWinchConnectionManager manager = ((ServerGrappleWinchConnectionManager.Access) player.getServerWorld()).klaxon$get();
            @Nullable ServerGrappleWinchConnection connection = manager.fromPlayer(this.player);
            if (connection != null) {
                connection.resetCableLength();
            }
        }
    }
}
