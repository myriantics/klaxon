package net.myriantics.klaxon.mixin.minecraft.unenchantability;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    // when I say unenchantable i mean UNENCHANTABLE

    @ModifyReturnValue(
            method = "canEnchant",
            at = @At(value = "RETURN")
    )
    public boolean klaxon$unenchantableOverride1(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return original && !stack.is(KlaxonItemTags.UNENCHANTABLE);
    }

    @ModifyReturnValue(
            method = "isSupportedItem",
            at = @At(value = "RETURN")
    )
    public boolean klaxon$unenchantableOverride2(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return original && !stack.is(KlaxonItemTags.UNENCHANTABLE);
    }
}
