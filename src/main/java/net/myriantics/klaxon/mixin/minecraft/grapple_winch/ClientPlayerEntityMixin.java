package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.myriantics.klaxon.client.GrappleWinchClientConnectionManager;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchConnectionData;
import net.myriantics.klaxon.mechanics.item_usage_lockout.MinecraftClientUsageLockoutAccess;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(
            method = "sendMovementPackets",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0)
    )
    public void klaxon$resetGrappleWinchTargetPosition(CallbackInfo ci) {
        PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) this;

        if (!isOnGround()) {
            if (access.klaxon$hasActiveConnection()) {
                access.klaxon$resetWinchCableLength();
            }
        }
    }
}
