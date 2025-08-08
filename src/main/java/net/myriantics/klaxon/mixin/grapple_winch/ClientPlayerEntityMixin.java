package net.myriantics.klaxon.mixin.grapple_winch;

import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.util.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(
            method = "sendMovementPackets",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0)
    )
    public void klaxon$resetGrappleWinchTargetPosition(CallbackInfo ci) {
        if (this instanceof PlayerEntityGrappleAccess access) {
            GrappleClawEntity grappleClaw = access.klaxon$getGrappleClaw();
            if (grappleClaw != null) grappleClaw.resetTargetRangeSquared();
        }
    }
}
