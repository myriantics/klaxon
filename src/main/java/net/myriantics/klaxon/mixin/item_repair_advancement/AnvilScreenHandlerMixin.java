package net.myriantics.klaxon.mixin.item_repair_advancement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Shadow private int repairItemUsage;

    @Inject(
            method = "onTakeOutput",
            at = @At(value = "HEAD")
    )
    public void klaxon$repairAdvancementHook(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            // check that we have actually done a repairing recipe before firing advancement
            if (repairItemUsage > 0 || input.getStack(0).getItem().equals(input.getStack(1).getItem())) {
                KlaxonAdvancementTriggers.triggerItemRepair(serverPlayer, stack);
            }
        }
    }
}
