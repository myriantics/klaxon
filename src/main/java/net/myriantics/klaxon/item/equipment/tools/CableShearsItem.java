package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

import java.util.ArrayList;

public class CableShearsItem extends ShearsItem {

    private final ToolMaterial material;

    public CableShearsItem(ToolMaterial material, Settings settings) {
        super(settings
                .component(DataComponentTypes.MAX_DAMAGE, material.getDurability())
                .component(DataComponentTypes.TOOL, tupleToolComponent(ShearsItem.createToolComponent(), material, KlaxonBlockTags.CABLE_SHEARS_MINEABLE))
                .component(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG, new ToolUseRecipeConfigComponent(KlaxonSoundEvents.ITEM_CABLE_SHEARS_USAGE))
                .component(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT, new InstabreakingToolComponent(KlaxonBlockTags.CABLE_SHEARS_INSTABREAKABLE))
        );
        this.material = material;
    }

    private static ToolComponent tupleToolComponent(ToolComponent shears, ToolMaterial material, TagKey<Block> effectiveBlocks) {
        ArrayList<ToolComponent.Rule> rules = new ArrayList<>(shears.rules());
        ToolComponent fromMaterial = material.createComponent(effectiveBlocks);
        rules.addAll(fromMaterial.rules());
        return new ToolComponent(rules, fromMaterial.defaultMiningSpeed(), fromMaterial.damagePerBlock());
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
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(2, attacker, EquipmentSlot.MAINHAND);
    }
}
