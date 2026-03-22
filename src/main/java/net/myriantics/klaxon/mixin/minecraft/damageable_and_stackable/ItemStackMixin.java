package net.myriantics.klaxon.mixin.minecraft.damageable_and_stackable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyReturnValue(
            method = "validateComponents",
            at = @At(value = "RETURN", ordinal = 0)
    )
    private static DataResult<Unit> klaxon$checkForAllowlistComponent(DataResult<Unit> original, @Local(argsOnly = true) DataComponentMap components) {
        if (original.isError() && components.has(KlaxonDataComponentTypes.DAMAGEABLE_AND_STACKABLE.value())) {
            // erm... actually... this item CAN IN FACT be both damageable and stackable...
            return DataResult.success(Unit.INSTANCE);
        }

        return original;
    }
}
