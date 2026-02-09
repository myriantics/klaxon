package net.myriantics.klaxon.mixin.minecraft.datagen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.ArmorItem;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {
    @ModifyExpressionValue(
            method = "registerArmor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry;matches(Lnet/minecraft/registry/entry/RegistryEntry;)Z")
    )
    private boolean klaxon$generateDyedStuffForCrestedSteelHelmetPlsThankU(
            boolean original,
            @Local(argsOnly = true) ArmorItem armorItem
    ) {
        return original || armorItem == KlaxonItems.CRESTED_STEEL_HELMET;
    }
}
