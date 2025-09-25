package net.myriantics.klaxon.mixin.minecraft.item_usage_lockout;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.myriantics.klaxon.mechanics.item_usage_lockout.MinecraftClientUsageLockoutAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow
    private boolean usingItem;

    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(
            method = "setCurrentHand",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;activeHand:Lnet/minecraft/util/Hand;")
    )
    private void klaxon$cancelItemUsageIfLockoutIsActive(Hand hand, CallbackInfo ci) {
        usingItem &= !(client instanceof MinecraftClientUsageLockoutAccess access && access.klaxon$isUsageLockoutActive());
    }
}
