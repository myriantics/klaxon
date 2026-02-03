package net.myriantics.klaxon.mixin.minecraft.reinforced_flint_and_steel;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(CandleCakeBlock.class)
public abstract class CandleCakeMixin {
    @WrapOperation(
            method = "onUseWithItem",
            slice = @Slice(
                    to = @At(value = "FIELD", target = "Lnet/minecraft/util/ItemActionResult;SKIP_DEFAULT_BLOCK_INTERACTION:Lnet/minecraft/util/ItemActionResult;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0)
    )
    private boolean klaxon$checkForReinforcedFlintAndSteel(ItemStack instance, Item item, Operation<Boolean> original) {
        return instance.isOf(KlaxonItems.REINFORCED_FLINT_AND_STEEL) || original.call(instance, item);
    }
}
