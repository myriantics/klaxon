package net.myriantics.klaxon.mixin.item_components.walljump_ability;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.hit.BlockHitResult;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.networking.c2s.HammerWalljumpTriggerPacket;
import net.myriantics.klaxon.util.DualWieldHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    // this is used to detect the rising edge of a block attack, because that's the only time i want the hammer walljump triggered
    @ModifyExpressionValue(
            method = "doAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isAir()Z", ordinal = 0)
    )
    public boolean klaxon$risingEdgeBlockAttackCheck(boolean original, @Local BlockHitResult hitResult) {
        if (interactionManager != null && player != null && WalljumpAbilityComponent.canWallJump(player, player.getMainHandStack(), player.getWorld().getBlockState(hitResult.getBlockPos()))) {

            WalljumpAbilityComponent mainHandComponent = WalljumpAbilityComponent.get(player.getMainHandStack());

            // run walljump on client side
            if (mainHandComponent != null) {
                mainHandComponent.processHammerWalljump(player, player.getWorld(), hitResult.getBlockPos(), hitResult.getSide());

                // trigger animation for dual-wield walljumping if offhand component is present
                if (WalljumpAbilityComponent.get(player.getOffHandStack()) != null) {
                    DualWieldHelper.setDualWielding(player, true);
                }
            }

            // send packet that triggers hammer walljump on the server side
            ClientPlayNetworking.send(new HammerWalljumpTriggerPacket(hitResult.getBlockPos(), hitResult.getSide()));
        }
        return original;
    }
}
