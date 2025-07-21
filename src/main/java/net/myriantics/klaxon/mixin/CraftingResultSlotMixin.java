package net.myriantics.klaxon.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RepairItemRecipe;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin {


    @Shadow @Final private PlayerEntity player;

    // dude figuring out where to put this made me go through the 5 stages of grief
    // then I finally get it and feel like a giga hackerman
    @Inject(
            method = "onCrafted(Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;onCraftByPlayer(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V")
    )
    public void klaxon$triggerItemRepairAdvancementCriterion(ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer)  {
            CraftingResultSlot self = (CraftingResultSlot) (Object) this;
            if (self.inventory instanceof CraftingResultInventory craftingResultInventory
                    && craftingResultInventory.getLastRecipe() != null
                    && craftingResultInventory.getLastRecipe().value() instanceof RepairItemRecipe) {
                KlaxonAdvancementTriggers.triggerItemRepair(serverPlayer, stack);
            }
        }
    }
}
