package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_winch;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(
            method = "remove",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;hasVehicle()Z")
    )
    private void klaxon$markConnectionAsDormantWhenLoggingOut(ServerPlayerEntity player, CallbackInfo ci) {
        @Nullable ServerGrappleWinchConnection connection = ((ServerGrappleWinchConnectionManager.Access) player.getServerWorld()).klaxon$get().fromPlayer(player);
        if (connection != null) {
            connection.makeDormant();
        }
    }
}
