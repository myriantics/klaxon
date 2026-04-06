package net.myriantics.klaxon.mixin.minecraft.reinforced_flint_and_steel;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.TntBlock;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin {
    @WrapOperation(
            method = "useItemOn",
            slice = @Slice(
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0)
    )
    private boolean klaxon$checkForReinforcedFlintAndSteel(ItemStack instance, Item item, Operation<Boolean> original) {
        return instance.is(KlaxonItems.STEEL_LIGHTER) || original.call(instance, item);
    }

    @WrapOperation(
            method = "useItemOn",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private boolean klaxon$checkForReinforcedFlintAndSteelAgain(ItemStack instance, Item item, Operation<Boolean> original) {
        return instance.is(KlaxonItems.STEEL_LIGHTER) || original.call(instance, item);
    }
}
