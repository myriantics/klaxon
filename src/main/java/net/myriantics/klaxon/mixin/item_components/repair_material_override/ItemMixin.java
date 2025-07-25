package net.myriantics.klaxon.mixin.item_components.repair_material_override;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.component.configuration.RepairIngredientOverrideComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin {

    @ModifyReturnValue(
            method = "canRepair",
            at =  @At(value = "RETURN")
    )
    public boolean klaxon$applyRepairIngredientOverride(
            boolean original,
            @Local(ordinal = 0, argsOnly = true) ItemStack repairedStack,
            @Local(ordinal = 1, argsOnly = true) ItemStack repairIngredientStack
    ) {
        // we completely override whatever the other ingredient could be if the component's present.
        // it's called an override for a reason.
        RepairIngredientOverrideComponent repairIngredientOverrideComponent = RepairIngredientOverrideComponent.get(repairedStack);
        if (repairIngredientOverrideComponent != null) {
            return repairIngredientOverrideComponent.repairMaterial().test(repairIngredientStack);
        }

        return original;
    }
}
