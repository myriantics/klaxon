package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.ClientPlayerEntityUsageAccess;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.MinecraftClientUsageLockoutAccess;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements ClientPlayerEntityUsageAccess {

    @Shadow
    private boolean usingItem;

    @Shadow
    @Final
    protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    public void klaxon$setUsingItem(boolean usingItem) {
        this.usingItem = usingItem;
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

    @Inject(
            method = "setCurrentHand",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;activeHand:Lnet/minecraft/util/Hand;")
    )
    private void klaxon$cancelItemUsageIfLockoutIsActive(Hand hand, CallbackInfo ci) {
        usingItem &= !(client instanceof MinecraftClientUsageLockoutAccess access && access.klaxon$isUsageLockoutActive());
    }
}
