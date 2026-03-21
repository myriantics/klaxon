package net.myriantics.klaxon.item.equipment.armor;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonAttributeModifierComponentModifications;

public class SteelArmorItem extends ArmorItem {

    public static final float MOVEMENT_SPEED_MULTIPLIER = -0.04f;

    public static final ResourceLocation MOVEMENT_SPEED_MULTIPLIER_ID = KlaxonCommon.locate("steel_armor_movement_speed_multiplier_id");

    public SteelArmorItem(Holder<ArmorMaterial> material, Type type, Properties settings) {
        super(material, type, settings.durability(type.getDurability(32)).stacksTo(1));
    }

    public static ItemAttributeModifiers appendAttributeModifiers(ItemAttributeModifiers attributeModifiers, EquipmentSlotGroup slot) {

        return attributeModifiers
                .withModifierAdded(
                        Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(KlaxonAttributeModifierComponentModifications.idFromSlot(MOVEMENT_SPEED_MULTIPLIER_ID, slot), MOVEMENT_SPEED_MULTIPLIER, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        slot
                );
    }
}
