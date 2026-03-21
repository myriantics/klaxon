package net.myriantics.klaxon.mixin.minecraft.item_repair_advancement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {

    public AnvilScreenHandlerMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(type, syncId, playerInventory, context);
    }

    @Shadow private int repairItemCountCost;

    @Inject(
            method = "onTake",
            at = @At(value = "HEAD")
    )
    public void klaxon$repairAdvancementHook(Player player, ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer) {
            // check that we have actually done a repairing recipe before firing advancement
            if (repairItemCountCost > 0 || inputSlots.getItem(0).getItem().equals(inputSlots.getItem(1).getItem())) {
                KlaxonAdvancementTriggers.triggerItemRepair(serverPlayer, stack);
            }
        }
    }
}
