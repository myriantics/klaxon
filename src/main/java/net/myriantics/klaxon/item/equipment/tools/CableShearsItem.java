package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.InstabreakMiningToolItem;
import net.myriantics.klaxon.component.configuration.ToolUseRecipeConfigComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public class CableShearsItem extends InstabreakMiningToolItem {

    public static final ToolComponent SHEARS_TOOL_COMPONENT = ShearsItem.createToolComponent();

    public static final float BASE_ATTACK_DAMAGE = 3.0F;
    public static final float BASE_ATTACK_SPEED = -2.6F;

    public CableShearsItem(ToolMaterial material, Settings settings) {
        super(material, KlaxonBlockTags.CABLE_SHEARS_MINEABLE, settings
                .component(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG, new ToolUseRecipeConfigComponent(SoundEvents.BLOCK_CHAIN_BREAK))
        );
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
        return state.isIn(KlaxonBlockTags.CABLE_SHEARS_INSTABREAKABLE);
    }

    // I... am Shears.

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult shearsResult = Items.SHEARS.useOnBlock(context);
        return shearsResult.equals(ActionResult.PASS) ? super.useOnBlock(context) : shearsResult;
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float shearsSpeed = SHEARS_TOOL_COMPONENT.getSpeed(state);
        float cableShearsSpeed = super.getMiningSpeed(stack, state);
        return Math.max(shearsSpeed, cableShearsSpeed);
    }

    @Override
    public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        if (SHEARS_TOOL_COMPONENT.isCorrectForDrops(state)) return true;
        return super.isCorrectForDrops(stack, state);
    }
}
