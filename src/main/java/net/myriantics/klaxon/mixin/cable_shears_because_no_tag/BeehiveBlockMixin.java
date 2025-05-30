package net.myriantics.klaxon.mixin.cable_shears_because_no_tag;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.item.equipment.tools.CableShearsItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin {

    @ModifyExpressionValue(
            method = "onUseWithItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z",
            ordinal = 0)
    )
    public boolean klaxon$cableShearsOverride(boolean original, @Local(argsOnly = true) ItemStack usedStack) {
        return original || usedStack.isIn(KlaxonItemTags.CABLE_SHEARS);
    }
}
