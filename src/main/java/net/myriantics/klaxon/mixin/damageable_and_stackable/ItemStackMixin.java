package net.myriantics.klaxon.mixin.damageable_and_stackable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyReturnValue(
            method = "validateComponents",
            at = @At(value = "RETURN", ordinal = 0)
    )
    private static DataResult<Unit> klaxon$checkForAllowlistComponent(DataResult<Unit> original, @Local(argsOnly = true) ComponentMap components) {
        if (original.isError() && components.contains(KlaxonDataComponentTypes.DAMAGEABLE_AND_STACKABLE)) {
            // erm... actually... this item CAN IN FACT be both damageable and stackable...
            return DataResult.success(Unit.INSTANCE);
        }

        return original;
    }
}
