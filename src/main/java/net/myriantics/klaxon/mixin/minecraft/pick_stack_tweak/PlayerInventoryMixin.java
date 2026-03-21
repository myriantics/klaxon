package net.myriantics.klaxon.mixin.minecraft.pick_stack_tweak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin {
    @WrapOperation(
            method = "getSuitableHotbarSlot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEnchanted()Z")
    )
    private boolean klaxon$dontOverwriteStacksWithInnateEnchantments(ItemStack instance, Operation<Boolean> original) {
        // treat items in the tag as they would be treated if enchanted
        return original.call(instance) || instance.is(KlaxonItemTags.PICK_BLOCK_SLOT_REPLACEMENT_DISCOURAGED);
    }
}
