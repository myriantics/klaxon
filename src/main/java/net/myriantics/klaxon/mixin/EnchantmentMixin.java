package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    // when I say unenchantable i mean UNENCHANTABLE

    @ModifyReturnValue(
            method = "isAcceptableItem",
            at = @At(value = "RETURN")
    )
    public boolean klaxon$unenchantableOverride1(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return original && !stack.isIn(KlaxonConventionalItemTags.UNENCHANTABLE);
    }

    @ModifyReturnValue(
            method = "isSupportedItem",
            at = @At(value = "RETURN")
    )
    public boolean klaxon$unenchantableOverride2(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return original && !stack.isIn(KlaxonConventionalItemTags.UNENCHANTABLE);
    }
}
