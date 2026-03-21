package net.myriantics.klaxon.mixin.minecraft.unenchantability;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin {
    @ModifyExpressionValue(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;canStoreEnchantments(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    public boolean klaxon$unenchantableOverride(boolean original, @Local ItemStack inputStack) {
        // this way anvils are still allowed to be used but anything else that calls the canHaveEnchantments methods get denied
        return original || inputStack.is(KlaxonItemTags.UNENCHANTABLE);
    }

    @ModifyExpressionValue(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamageableItem()Z", ordinal = 1)
    )
    public boolean klaxon$repairOverride(boolean original, @Local(ordinal = 0) ItemStack inputStack) {
        // doesnt allow enchants to be applied to unenchantable items
        return original || !inputStack.is(KlaxonItemTags.UNENCHANTABLE);
    }
}
