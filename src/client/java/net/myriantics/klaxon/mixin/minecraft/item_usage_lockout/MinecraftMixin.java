package net.myriantics.klaxon.mixin.minecraft.item_usage_lockout;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.myriantics.klaxon.mechanics.item_usage_lockout.MinecraftClientUsageLockoutAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftClientUsageLockoutAccess {
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
            method = "handleKeybinds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startUseItem()V", ordinal = 0)
    )
    private void klaxon$resetUsageLockoutIfNeeded(CallbackInfo ci) {
        klaxon$usageLockoutTriggered = false;
    }

    @WrapOperation(
            method = "handleKeybinds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startUseItem()V")
    )
    private void klaxon$preventSustainedUseIfItemUsageLockoutIsActive(Minecraft instance, Operation<Void> original) {
        if (!klaxon$usageLockoutTriggered) {
            original.call(instance);
        }
    }
}
