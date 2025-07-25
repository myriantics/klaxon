package net.myriantics.klaxon.mixin.cable_shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PumpkinBlock.class)
public abstract class PumpkinBlockMixin {

    @ModifyExpressionValue(
            method = "onUseWithItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    public boolean klaxon$cableShearsOverride(boolean original, @Local(argsOnly = true) ItemStack usedStack) {
        return original || usedStack.isIn(KlaxonItemTags.CABLE_SHEARS);
    }
}
