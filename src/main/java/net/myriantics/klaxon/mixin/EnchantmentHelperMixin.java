package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    // i really do truly mean unenchantable.

    @Inject(
            method = "getPossibleEntries",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void klaxon$unenchantableOverride(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (stack.isIn(KlaxonItemTags.UNENCHANTABLE)) cir.setReturnValue(List.of());
    }

    @ModifyReturnValue(
            method = "getLevel",
            at = @At(value = "RETURN")
    )
    private static int klaxon$innateUnbreakingOverride(int original, @Local(argsOnly = true) Enchantment enchantment, @Local(argsOnly = true) ItemStack stack) {
        boolean shouldOverride = enchantment.equals(Enchantments.UNBREAKING) && stack.isIn(KlaxonItemTags.INNATE_UNBREAKING_EQUIPMENT);
        return shouldOverride ? 4 : original;
    }
}
