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

    @Mutable
    @Shadow @Final private ComponentMap components;

    @Unique
    private DynamicRegistryManager lastUsedRegistryManager;

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

    @ModifyReturnValue(
            method = "getComponents",
            at = @At(value = "RETURN")
    )
    public ComponentMap klaxon$bakeInnateEnchantmentComponent(ComponentMap original) {
        if (
                // make sure that a registry manager has loaded so that we can actually bake some components
                PrebakedInnateItemEnchantmentsComponent.getRegistryManager() != null
                // make sure we actually have prebaked innate enchantments to bake before checking anything else
                && original.get(KlaxonDataComponentTypes.PREBAKED_INNATE_ENCHANTMENTS) instanceof PrebakedInnateItemEnchantmentsComponent component
                        && (
                        // This condition is very important - without it, items won't refresh their baked innate enchantments when swapping worlds - causing certain innate enchantments such as looting and silk touch to not function.
                        // basically just checking that our baked component is up-to-date
                        !PrebakedInnateItemEnchantmentsComponent.getRegistryManager().equals(lastUsedRegistryManager)
                        // if an item has an innate enchantments component as a default component, that's because we've already baked it - we shouldn't overwrite it.
                        || !original.contains(KlaxonDataComponentTypes.INNATE_ENCHANTMENTS)
                )
        ) {
            ComponentMap computed = ComponentMap.builder().addAll(original).addAll(component.bake()).build();
            this.components = computed;

            // indicate what registry manager we last used
            this.lastUsedRegistryManager = PrebakedInnateItemEnchantmentsComponent.getRegistryManager();
            return computed;
        }

        return original;
    }
}
