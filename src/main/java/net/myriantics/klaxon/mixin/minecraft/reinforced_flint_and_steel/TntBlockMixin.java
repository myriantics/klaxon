package net.myriantics.klaxon.mixin.minecraft.reinforced_flint_and_steel;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.TntBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin {
    @WrapOperation(
            method = "onUseWithItem",
            slice = @Slice(
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0)
    )
    private boolean klaxon$checkForReinforcedFlintAndSteel(ItemStack instance, Item item, Operation<Boolean> original) {
        return instance.isOf(KlaxonItems.REINFORCED_FLINT_AND_STEEL) || original.call(instance, item);
    }

    @WrapOperation(
            method = "onUseWithItem",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private boolean klaxon$checkForReinforcedFlintAndSteelAgain(ItemStack instance, Item item, Operation<Boolean> original) {
        return instance.isOf(KlaxonItems.REINFORCED_FLINT_AND_STEEL) || original.call(instance, item);
    }
}
