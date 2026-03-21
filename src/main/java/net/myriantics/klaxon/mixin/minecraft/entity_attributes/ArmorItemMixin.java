package net.myriantics.klaxon.mixin.minecraft.entity_attributes;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.myriantics.klaxon.registry.item.KlaxonAttributeModifierComponentModifications;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin {

    @ModifyExpressionValue(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/google/common/base/Suppliers;memoize(Lcom/google/common/base/Supplier;)Lcom/google/common/base/Supplier;"),
            remap = false
    )
    private com.google.common.base.Supplier<ItemAttributeModifiers> klaxon$appendSteelArmorAttributeModifiers(com.google.common.base.Supplier<ItemAttributeModifiers> original, @Local(argsOnly = true) Holder<ArmorMaterial> material, @Local(argsOnly = true) ArmorItem.Type type) {
        return () -> KlaxonAttributeModifierComponentModifications.applyArmorModifications((ArmorItem)(Object)this, original.get());
    }
}
