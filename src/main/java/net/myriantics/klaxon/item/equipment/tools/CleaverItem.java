package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.myriantics.klaxon.api.InstabreakMiningToolItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public class CleaverItem extends InstabreakMiningToolItem {
    public CleaverItem(ToolMaterial material, Settings settings) {
        super(material, KlaxonBlockTags.CLEAVER_MINEABLE, settings);
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
        return state.isIn(KlaxonBlockTags.CLEAVER_INSTABREAKABLE);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return Items.DIAMOND_AXE.useOnBlock(context);
    }
}
