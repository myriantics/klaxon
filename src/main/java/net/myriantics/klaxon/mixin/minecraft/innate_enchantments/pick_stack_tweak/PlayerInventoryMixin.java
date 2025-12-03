package net.myriantics.klaxon.mixin.minecraft.innate_enchantments.pick_stack_tweak;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.component.configuration.InnateItemEnchantmentsComponent;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @WrapOperation(
            method = "getSwappableHotbarSlot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasEnchantments()Z")
    )
    private boolean klaxon$dontOverwriteStacksWithInnateEnchantments(ItemStack instance, Operation<Boolean> original) {
        return original.call(instance) || !instance.getOrDefault(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS, InnateItemEnchantmentsComponent.DEFAULT).enchantments().isEmpty();
    }
}
