package net.myriantics.klaxon.mixin.dual_wielding;

import net.minecraft.client.MinecraftClient;
import net.myriantics.klaxon.util.DualWieldHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(
            method = "doAttack",
            at = @At(value = "HEAD")
    )
    private void klaxon$resetDualWieldingOnAttack(CallbackInfoReturnable<Boolean> cir) {
        DualWieldHelper.setDualWielding(MinecraftClient.getInstance().player, false);
    }

    @Inject(
            method = "doItemUse",
            at = @At(value = "HEAD")
    )
    private void klaxon$resetDualWieldingOnItemUse(CallbackInfo ci) {
        DualWieldHelper.setDualWielding(MinecraftClient.getInstance().player, false);
    }
}
