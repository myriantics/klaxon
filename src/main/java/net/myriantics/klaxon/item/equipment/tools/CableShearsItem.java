package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

import java.util.ArrayList;

public class CableShearsItem extends ShearsItem {

    private final Tier material;

    public CableShearsItem(Tier material, Properties settings) {
        super(settings
                .durability(material.getUses())
                .component(DataComponents.TOOL, tupleToolComponent(ShearsItem.createToolProperties(), material, KlaxonBlockTags.CABLE_SHEARS_MINEABLE))
                .component(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG, new ToolUseRecipeConfigComponent(KlaxonSoundEvents.ITEM_CABLE_SHEARS_USAGE))
                .component(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT, new InstabreakingToolComponent(KlaxonBlockTags.CABLE_SHEARS_INSTABREAKABLE))
        );
        this.material = material;
    }

    private static Tool tupleToolComponent(Tool shears, Tier material, TagKey<Block> effectiveBlocks) {
        ArrayList<Tool.Rule> rules = new ArrayList<>(shears.rules());
        Tool fromMaterial = material.createToolProperties(effectiveBlocks);
        rules.addAll(fromMaterial.rules());
        return new Tool(rules, fromMaterial.defaultMiningSpeed(), fromMaterial.damagePerBlock());
    }

    public static ItemAttributeModifiers createAttributeModifiers(Tier material, float baseAttackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, material.getAttackDamageBonus() + baseAttackDamage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                ).build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.isValidRepairItem(stack, ingredient);
    }
}
