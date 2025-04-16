package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.myriantics.klaxon.item.equipment.tools.CleaverItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.util.DurabilityHelper;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    // i really do truly mean unenchantable.

    @ModifyReturnValue(
            method = "canHaveEnchantments",
            at = @At(value = "RETURN")
    )
    private static boolean klaxon$unenchantableOverride(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return original && !stack.isIn(KlaxonItemTags.UNENCHANTABLE);
    }

    @ModifyVariable(
            method = "getItemDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;)V"))
    private static MutableFloat klaxon$innateUnbreakingOverride(MutableFloat originalDamage, @Local(argsOnly = true) ServerWorld serverWorld, @Local(argsOnly = true) ItemStack stack) {
        return stack.isIn(KlaxonItemTags.INNATE_UNBREAKING_EQUIPMENT) ? DurabilityHelper.applyInnateUnbreaking(stack, serverWorld, originalDamage) : originalDamage;
    }

    @ModifyReturnValue(
            method = "getLevel",
            at = @At(value = "RETURN")
    )
    private static int klaxon$innateLootingOverride(int original, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) RegistryEntry<Enchantment> enchantment) {
        // hardcoded because i cant be bothered rn
        if (enchantment.getKey().isPresent() && enchantment.getKey().get().isOf(Enchantments.LOOTING.getRegistryRef()) && stack.getItem() instanceof CleaverItem) {
            return original + 1;
        }

        return original;
    }
}
