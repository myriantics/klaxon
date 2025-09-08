package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.myriantics.klaxon.util.grapple_winch.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(
            method = "sendMovementPackets",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0)
    )
    public void klaxon$resetGrappleWinchTargetPosition(CallbackInfo ci) {
        if (!isOnGround() && this instanceof PlayerEntityGrappleAccess access) {
            if (access.klaxon$hasActiveConnection()) {
                access.klaxon$resetWinchCableLength();
            }
        }
    }
}
