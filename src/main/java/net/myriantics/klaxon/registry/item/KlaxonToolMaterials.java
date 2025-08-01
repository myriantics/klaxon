package net.myriantics.klaxon.registry.item;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public abstract class KlaxonToolMaterials {
    public static final ToolMaterial STEEL = registerToolMaterial(640, 6.0f, 3.0f, KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL, 0, Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS));
    public static final ToolMaterial STEEL_PLATE = registerToolMaterial(640, 6.0f, 3.0f, KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL, 0, Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES));
    public static final ToolMaterial STEEL_NUGGET = registerToolMaterial(128, 6.0f, 3.0f, KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL, 0, Ingredient.fromTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS));

    private static ToolMaterial registerToolMaterial(
            int durability,
            float miningSpeedMultiplier,
            float attackDamage,
            TagKey<Block> inverseTag,
            int enchantability,
            Ingredient repairIngredient) {
        return new ToolMaterial() {
            @Override
            public int getDurability() {
                return durability;
            }

            @Override
            public float getMiningSpeedMultiplier() {
                return miningSpeedMultiplier;
            }

            @Override
            public float getAttackDamage() {
                return attackDamage;
            }

            @Override
            public TagKey<Block> getInverseTag() {
                return inverseTag;
            }

            @Override
            public int getEnchantability() {
                return enchantability;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return repairIngredient;
            }
        };
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Tool Materials!");
    }
}
