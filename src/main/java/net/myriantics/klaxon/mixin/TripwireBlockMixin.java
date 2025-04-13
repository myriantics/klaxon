package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.TripwireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.item.equipment.tools.CableShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TripwireBlock.class)
public class TripwireBlockMixin {

    @ModifyExpressionValue(
            method = "onBreak",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    public boolean klaxon$cableShearsOverride(boolean original, @Local(argsOnly = true) PlayerEntity player) {
        ItemStack miningToolStack = player.getMainHandStack();

        return original || miningToolStack.getItem() instanceof CableShearsItem;
    }
}
