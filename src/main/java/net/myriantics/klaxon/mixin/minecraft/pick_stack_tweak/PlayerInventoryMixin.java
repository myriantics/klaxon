package net.myriantics.klaxon.mixin.minecraft.pick_stack_tweak;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @WrapOperation(
            method = "getSwappableHotbarSlot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasEnchantments()Z")
    )
    private boolean klaxon$dontOverwriteStacksWithInnateEnchantments(ItemStack instance, Operation<Boolean> original) {
        // treat items in the tag as they would be treated if enchanted
        return original.call(instance) || instance.isIn(KlaxonItemTags.PICK_BLOCK_SLOT_REPLACEMENT_DISCOURAGED);
    }
}
