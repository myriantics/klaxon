package net.myriantics.klaxon.mixin.self.lighter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.myriantics.klaxon.item.equipment.tools.LighterItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LighterItem.class)
public abstract class LighterItemMixin {
    @Inject(
            method = "onUseTick",
            at = @At(value = "TAIL")
    )
    private void klaxon$sendPacketForUsage(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration, CallbackInfo ci) {
        if (livingEntity instanceof LocalPlayer localPlayer) {
            MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
            HitResult hitResult = Minecraft.getInstance().hitResult;
            if (gameMode != null && hitResult != null) {
                switch (hitResult.getType()) {
                    case BLOCK -> gameMode.useItemOn(localPlayer, localPlayer.getUsedItemHand(), (BlockHitResult) hitResult);
                    case ENTITY -> gameMode.interactAt(localPlayer, ((EntityHitResult) hitResult).getEntity(), (EntityHitResult) hitResult, localPlayer.getUsedItemHand());
                    default -> {}
                }
            }
        }
    }
}
