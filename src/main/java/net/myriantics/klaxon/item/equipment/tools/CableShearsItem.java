package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.myriantics.klaxon.api.InstabreakMiningToolItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class CableShearsItem extends InstabreakMiningToolItem {

    public static final float BASE_ATTACK_DAMAGE = 3.0F;
    public static final float BASE_ATTACK_SPEED = -2.6F;

    public CableShearsItem(ToolMaterial material, Settings settings) {
        super(material, KlaxonBlockTags.CABLE_SHEARS_MINEABLE, settings);
    }

    public static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, BASE_ATTACK_DAMAGE, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, BASE_ATTACK_SPEED, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                ).build();
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES);
    }

    @Override
    public boolean isCorrectForInstabreak(ItemStack stack, BlockState state) {
        return state.isIn(KlaxonBlockTags.CABLE_SHEARS_INSTABREAKABLE);
    }
}
