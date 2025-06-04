package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.component.configuration.PrebakedInnateItemEnchantmentsComponent;
import net.myriantics.klaxon.component.configuration.RepairIngredientOverrideComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin {

    @ModifyReturnValue(
            method = "canRepair",
            at =  @At(value = "RETURN")
    )
    public boolean klaxon$flintAndSteelRepairItemOverride(
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

    @ModifyReturnValue(
            method = "canMine",
            at = @At(value = "RETURN")
    )
    public boolean klaxon$walljumpAbilityMiningRestrictionOverride(boolean original, @Local(argsOnly = true) PlayerEntity miner) {
        // if it restricts mining, say that you can't mine.
        if (!WalljumpAbilityComponent.allowsMining(miner)) return false;
        return original;
    }
}
