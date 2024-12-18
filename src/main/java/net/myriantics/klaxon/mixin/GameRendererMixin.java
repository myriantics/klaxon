package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.item.tools.HammerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    // right man i have to mixin to a lambda in the game renderer to get what i want
    // yeah sure
    // this is kind of meh because of the fact that you can't place blocks through dropped items while holding a hammer but eh
    // above statement mostly mitigated by canProcessHammerRecipe conditional
    @ModifyExpressionValue(
            method = "method_18144",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canHit()Z")
    )
    private static boolean checkForHammerOverride(boolean original, @Local(argsOnly = true) Entity entity) {
        if (!original && entity instanceof ItemEntity) {
            PlayerEntity player = MinecraftClient.getInstance().player;

            // if the player is holding a hammer at all, they can hit the item entity
            return player != null && HammerItem.canProcessHammerRecipe(player) && player.isHolding(KlaxonItems.STEEL_HAMMER);
        }
        return original;
    }
}
