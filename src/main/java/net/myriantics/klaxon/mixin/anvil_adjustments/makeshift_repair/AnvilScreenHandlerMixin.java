package net.myriantics.klaxon.mixin.anvil_adjustments.makeshift_repair;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {

    // halves durability gained from using makeshift repair materials to repair items

    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 0)
    )
    public int klaxon$makeshiftRepairMaterialOverride1(int original, @Local(ordinal = 2) ItemStack repairItem) {

        return repairItem.isIn(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS) ? original / 2 : original;
    }

    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 1)
    )
    public int klaxon$makeshiftRepairMaterialOverride2(int original, @Local(ordinal = 2) ItemStack repairItem) {

        return repairItem.isIn(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS) ? original / 2 : original;
    }
}
