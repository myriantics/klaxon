package net.myriantics.klaxon.mixin.minecraft.steel_lighter;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(CandleCakeBlock.class)
public abstract class CandleCakeMixin {
    @WrapOperation(
            method = "useItemOn",
            slice = @Slice(
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/ItemInteractionResult;SKIP_DEFAULT_BLOCK_INTERACTION:Lnet/minecraft/world/ItemInteractionResult;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0)
    )
    private boolean klaxon$checkForReinforcedFlintAndSteel(ItemStack instance, Item item, Operation<Boolean> original) {
        return instance.is(KlaxonItems.STEEL_LIGHTER) || original.call(instance, item);
    }
}
