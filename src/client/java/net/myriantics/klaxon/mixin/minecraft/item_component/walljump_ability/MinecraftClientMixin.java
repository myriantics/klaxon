package net.myriantics.klaxon.mixin.minecraft.item_component.walljump_ability;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.networking.c2s.HammerWalljumpTriggerPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public LocalPlayer player;

    @Shadow @Nullable public MultiPlayerGameMode gameMode;

    // this is used to detect the rising edge of a block attack, because that's the only time i want the hammer walljump triggered
    @ModifyExpressionValue(
            method = "startAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z", ordinal = 0)
    )
    public boolean klaxon$risingEdgeBlockAttackCheck(boolean original, @Local BlockHitResult hitResult) {
        if (gameMode != null && player != null && WalljumpAbilityComponent.canWallJump(player, player.getMainHandItem(), player.level().getBlockState(hitResult.getBlockPos()))) {

            WalljumpAbilityComponent mainHandComponent = WalljumpAbilityComponent.get(player.getMainHandItem());

            // run walljump on client side
            if (mainHandComponent != null) {
                mainHandComponent.processHammerWalljump(player, player.level(), hitResult.getBlockPos(), hitResult.getDirection());
            }

            // send packet that triggers hammer walljump on the server side
            ClientPlayNetworking.send(new HammerWalljumpTriggerPacket(hitResult.getBlockPos(), hitResult.getDirection()));
        }
        return original;
    }
}
