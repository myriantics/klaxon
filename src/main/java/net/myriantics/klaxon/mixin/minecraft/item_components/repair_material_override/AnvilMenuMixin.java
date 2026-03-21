package net.myriantics.klaxon.mixin.minecraft.item_components.repair_material_override;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.component.configuration.RepairIngredientOverrideComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @ModifyExpressionValue(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isValidRepairItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z")
    )
    public boolean klaxon$repairItemOverride(
            boolean original,
            @Local(ordinal = 1) ItemStack repairedStack,
            @Local(ordinal = 2) ItemStack repairIngredientStack
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
