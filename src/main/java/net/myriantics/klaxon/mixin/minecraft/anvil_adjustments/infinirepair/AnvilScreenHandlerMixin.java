package net.myriantics.klaxon.mixin.minecraft.anvil_adjustments.infinirepair;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin {
    @WrapOperation(
            method = "createResult",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;calculateIncreasedRepairCost(I)I"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/ItemEnchantments;)V")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private <T> T klaxon$anvilUsesOverride(ItemStack instance, DataComponentType<? super T> type, @Nullable T value, Operation<T> original) {
        return instance.is(KlaxonItemTags.INFINITELY_REPAIRABLE)
                ? value
                : original.call(instance, type, value);
    }
}
