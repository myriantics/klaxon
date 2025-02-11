package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.hit.BlockHitResult;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.networking.packets.HammerWalljumpTriggerPacket;
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
    public boolean risingEdgeBlockAttackCheck(boolean original, @Local BlockHitResult hitResult) {
        if (interactionManager != null && player != null && HammerItem.canWallJump(player, player.getWorld().getBlockState(hitResult.getBlockPos()))) {

            // run walljump on client side
            HammerItem.processHammerWalljump(player, player.getWorld(), hitResult.getBlockPos(), hitResult.getSide());

            // send packet that triggers hammer walljump on the server side
            ClientPlayNetworking.send(new HammerWalljumpTriggerPacket(hitResult.getBlockPos(), hitResult.getSide()));
        }
        return original;
    }
}
