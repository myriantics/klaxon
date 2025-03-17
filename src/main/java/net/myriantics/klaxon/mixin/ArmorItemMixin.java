package net.myriantics.klaxon.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.item.equipment.armor.KlaxonArmorMaterials;
import net.myriantics.klaxon.item.equipment.armor.SteelArmorItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.UUID;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin {

    @Shadow @Final private static EnumMap<ArmorItem.Type, UUID> MODIFIERS;

    @Inject(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMultimap$Builder;", ordinal = 1)
    )
    void klaxon$appendSteelArmorAttributeModifiers(ArmorMaterial material, ArmorItem.Type type, Item.Settings settings, CallbackInfo ci, @Local ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder) {
        // if it's steel armor, append steel armor modifiers
        if (material.equals(KlaxonArmorMaterials.STEEL)) {
            SteelArmorItem.appendAttributeModifiers(builder, MODIFIERS, material, type);
        }
    }
}
