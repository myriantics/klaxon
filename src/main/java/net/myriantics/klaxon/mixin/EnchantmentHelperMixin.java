package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.component.configuration.InnateItemEnchantmentsComponent;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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

    @ModifyReturnValue(
            method = "getLevel",
            at = @At(value = "RETURN")
    )
    private static int klaxon$innateEnchantmentsOverride(int original, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) RegistryEntry<Enchantment> enchantment) {
        InnateItemEnchantmentsComponent innate = InnateItemEnchantmentsComponent.get(stack);
        if (innate != null) {
            return original + innate.getLevel(enchantment);
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private static Object klaxon$forEachEnchantmentOverride1(Object original, @Local(argsOnly = true) ItemStack stack) {
        if (InnateItemEnchantmentsComponent.get(stack) instanceof InnateItemEnchantmentsComponent innate) {
            if (original instanceof ItemEnchantmentsComponent component && !component.isEmpty()) {
                ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(component);
                for (RegistryEntry<Enchantment> entry : innate.enchantments().getEnchantments()) {
                    builder.set(entry, innate.getLevel(entry) + component.getLevel(entry));
                }
                return builder.build();
            } else {
                return innate.enchantments();
            }
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/enchantment/EnchantmentHelper$ContextAwareConsumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;")
    )
    private static Object klaxon$forEachEnchantmentOverride2(Object original, @Local(argsOnly = true) ItemStack stack) {
        if (InnateItemEnchantmentsComponent.get(stack) instanceof InnateItemEnchantmentsComponent innate) {
            if (original instanceof ItemEnchantmentsComponent component && !component.isEmpty()) {
                ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(component);
                for (RegistryEntry<Enchantment> entry : innate.enchantments().getEnchantments()) {
                    builder.set(entry, innate.getLevel(entry) + component.getLevel(entry));
                }
                return builder.build();
            } else {
                return innate.enchantments();
            }
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "hasAnyEnchantmentsIn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private static Object klaxon$hasAnyEnchantmentsInOverride(Object original, @Local(argsOnly = true) ItemStack stack) {
        if (InnateItemEnchantmentsComponent.get(stack) instanceof InnateItemEnchantmentsComponent innate) {
            if (original instanceof ItemEnchantmentsComponent component && !component.isEmpty()) {
                ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(component);
                for (RegistryEntry<Enchantment> entry : innate.enchantments().getEnchantments()) {
                    builder.set(entry, innate.getLevel(entry) + component.getLevel(entry));
                }
                return builder.build();
            } else {
                return innate.enchantments();
            }
        }

        return original;
    }
}
