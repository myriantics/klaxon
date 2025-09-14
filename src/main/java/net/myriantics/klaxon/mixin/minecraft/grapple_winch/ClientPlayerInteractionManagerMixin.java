package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.MinecraftClientUsageLockoutAccess;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyExpressionValue(
            method = "method_41929",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;isCoolingDown(Lnet/minecraft/item/Item;)Z")
    )
    private boolean klaxon$checkForUsageLockout(boolean original, @Local ItemStack winchStack, @Local(argsOnly = true) PlayerEntity player) {
        if (
                !original
                        && winchStack.isOf(KlaxonItems.GRAPPLE_WINCH)
                        && client instanceof MinecraftClientUsageLockoutAccess access
                        && access.klaxon$isUsageLockoutActive()
        ) {
            player.clearActiveItem();
            return true;
        }

        return original;
    }
}
