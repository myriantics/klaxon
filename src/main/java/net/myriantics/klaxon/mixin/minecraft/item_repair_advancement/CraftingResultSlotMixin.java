package net.myriantics.klaxon.mixin.minecraft.item_repair_advancement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public abstract class CraftingResultSlotMixin {


    @Shadow @Final private Player player;

    // dude figuring out where to put this made me go through the 5 stages of grief
    // then I finally get it and feel like a giga hackerman
    @Inject(
            method = "checkTakeAchievements(Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;onCraftedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;I)V")
    )
    public void klaxon$triggerItemRepairAdvancementCriterion(ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer)  {
            ResultSlot self = (ResultSlot) (Object) this;
            if (self.container instanceof ResultContainer craftingResultInventory
                    && craftingResultInventory.getRecipeUsed() != null
                    && craftingResultInventory.getRecipeUsed().value() instanceof RepairItemRecipe) {
                KlaxonAdvancementTriggers.triggerItemRepair(serverPlayer, stack);
            }
        }
    }
}
