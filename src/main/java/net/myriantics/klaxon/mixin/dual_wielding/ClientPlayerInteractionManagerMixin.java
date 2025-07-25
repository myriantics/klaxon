package net.myriantics.klaxon.mixin.dual_wielding;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.myriantics.klaxon.util.DualWieldHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Inject(
            method = "syncSelectedSlot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V")
    )
    public void klaxon$resetDualWieldingOnSelectedSlotSwitch(CallbackInfo ci) {
        DualWieldHelper.setDualWielding(MinecraftClient.getInstance().player, false);
    }
}
