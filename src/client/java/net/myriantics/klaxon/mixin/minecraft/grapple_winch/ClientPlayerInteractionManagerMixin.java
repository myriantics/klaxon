package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.mechanics.item_usage_lockout.MinecraftClientUsageLockoutAccess;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyExpressionValue(
            method = "method_41929",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemCooldowns;isOnCooldown(Lnet/minecraft/world/item/Item;)Z")
    )
    private boolean klaxon$checkForUsageLockout(boolean original, @Local ItemStack winchStack, @Local(argsOnly = true) Player player) {
        if (
                !original
                        && winchStack.is(KlaxonItems.GRAPPLE_WINCH)
                        && minecraft instanceof MinecraftClientUsageLockoutAccess access
                        && access.klaxon$isUsageLockoutActive()
        ) {
            player.stopUsingItem();
            return true;
        }

        return original;
    }
}
