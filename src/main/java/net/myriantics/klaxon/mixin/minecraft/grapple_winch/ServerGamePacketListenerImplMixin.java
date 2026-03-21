package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(
            method = "handlePlayerCommand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setShiftKeyDown(Z)V", ordinal = 0)
    )
    public void klaxon$resetGrappleWinchTargetPosition(ServerboundPlayerCommandPacket packet, CallbackInfo ci) {
        if (!player.onGround()) {
            ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get(player.serverLevel());
            @Nullable ServerGrappleWinchConnection connection = manager.fromPlayer(this.player);
            if (connection != null) {
                connection.resetCableLength();
            }
        }
    }
}
