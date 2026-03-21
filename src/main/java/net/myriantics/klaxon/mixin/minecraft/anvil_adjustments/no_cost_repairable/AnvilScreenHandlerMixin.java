package net.myriantics.klaxon.mixin.minecraft.anvil_adjustments.no_cost_repairable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin {

    @Shadow
    @Final
    private DataSlot cost;

    @ModifyExpressionValue(
            method = "mayPickup",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;get()I", ordinal = 1)
    )
    public int klaxon$canTakeItemOverride(int original, @Local(argsOnly = true) boolean present) {
        // jank method of allowing items to be removed if they cost 0 levels
        if (original == 0 && present) {
            return 1;
        }
        return original;
    }

    @Inject(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;broadcastChanges()V")
    )
    public void klaxon$applyFreeRepairExpDiscount(CallbackInfo ci, @Local(ordinal = 0) ItemStack repairedStack) {
        // makes an item free to repair if it's in the tag
        if (repairedStack.is(KlaxonItemTags.NO_XP_COST_REPAIRABLE)) {
            cost.set(0);
        }
    }
}
