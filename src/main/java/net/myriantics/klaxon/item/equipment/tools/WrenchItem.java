package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.myriantics.klaxon.api.InstabreakMiningToolItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public class WrenchItem extends InstabreakMiningToolItem {
    public WrenchItem(ToolMaterial material, Settings settings) {
        super(material, KlaxonBlockTags.WRENCH_MINEABLE, settings);
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, material.getAttackDamage() + baseAttackDamage, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                ).build();
    }

    @Override
    public boolean isCorrectForInstabreak(ItemStack stack, BlockState state) {
        return state.isIn(KlaxonBlockTags.WRENCH_INSTABREAKABLE);
    }
}
