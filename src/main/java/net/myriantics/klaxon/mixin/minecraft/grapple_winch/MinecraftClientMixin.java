package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.MinecraftClientUsageLockoutAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements MinecraftClientUsageLockoutAccess {
    @Unique
    private boolean klaxon$usageLockoutTriggered = false;

    @Unique
    @Override
    public boolean klaxon$isUsageLockoutActive() {
        return klaxon$usageLockoutTriggered;
    }

    @Unique
    @Override
    public void klaxon$setUsageLockout(boolean lockout) {
        this.klaxon$usageLockoutTriggered = lockout;
    }

    @Inject(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V", ordinal = 0)
    )
    private void klaxon$resetUsageLockoutIfNeeded(CallbackInfo ci) {
        klaxon$usageLockoutTriggered = false;
    }
}
