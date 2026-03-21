package net.myriantics.klaxon.mixin.minecraft.datagen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.world.item.ArmorItem;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemModelGenerators.class)
public abstract class ItemModelGeneratorMixin {
    @ModifyExpressionValue(
            method = "generateArmorTrims",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/core/Holder;)Z")
    )
    private boolean klaxon$generateDyedStuffForCrestedSteelHelmetPlsThankU(
            boolean original,
            @Local(argsOnly = true) ArmorItem armorItem
    ) {
        return original || armorItem == KlaxonItems.CRESTED_STEEL_HELMET;
    }
}
