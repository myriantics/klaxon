package net.myriantics.klaxon.mixin.unenchantability;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;canHaveEnchantments(Lnet/minecraft/item/ItemStack;)Z")
    )
    public boolean klaxon$unenchantableOverride(boolean original, @Local ItemStack inputStack) {
        // this way anvils are still allowed to be used but anything else that calls the canHaveEnchantments methods get denied
        return original || inputStack.isIn(KlaxonItemTags.UNENCHANTABLE);
    }

    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamageable()Z", ordinal = 1)
    )
    public boolean klaxon$repairOverride(boolean original, @Local(ordinal = 0) ItemStack inputStack) {
        // doesnt allow enchants to be applied to unenchantable items
        return original || !inputStack.isIn(KlaxonItemTags.UNENCHANTABLE);
    }
}
