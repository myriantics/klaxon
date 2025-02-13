package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.item.equipment.armor.KlaxonArmorMaterials;
import net.myriantics.klaxon.item.equipment.armor.SteelArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin {

    @ModifyExpressionValue(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/google/common/base/Suppliers;memoize(Lcom/google/common/base/Supplier;)Lcom/google/common/base/Supplier;")
    )
    com.google.common.base.Supplier<AttributeModifiersComponent> klaxon$appendSteelArmorAttributeModifiers(com.google.common.base.Supplier<AttributeModifiersComponent> original, @Local(argsOnly = true) RegistryEntry<ArmorMaterial> material, @Local(argsOnly = true) ArmorItem.Type type) {
        // if it's steel armor, append steel armor modifiers
        if (material.equals(KlaxonArmorMaterials.STEEL)) {
            return () -> SteelArmorItem.appendAttributeModifiers(original.get(), AttributeModifierSlot.forEquipmentSlot(type.getEquipmentSlot()));
        }
        return original;
    }
}
