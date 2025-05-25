package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.myriantics.klaxon.component.configuration.InnateItemEnchantmentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentsPredicate.class)
public abstract class EnchantmentsPredicateMixin {

    @ModifyExpressionValue(
            method = "test(Lnet/minecraft/item/ItemStack;Lnet/minecraft/component/type/ItemEnchantmentsComponent;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/predicate/item/EnchantmentPredicate;test(Lnet/minecraft/component/type/ItemEnchantmentsComponent;)Z")
    )
    public boolean klaxon$testInnateEnchantmentComponent(boolean original, @Local(argsOnly = true) ItemStack stack, @Local EnchantmentPredicate looped) {
        InnateItemEnchantmentsComponent component = InnateItemEnchantmentsComponent.get(stack);

        if (component != null) {
            return original || looped.test(component.enchantments());
        }

        return original;
    }
}
