package net.myriantics.klaxon.registry.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public abstract class KlaxonToolMaterials {
    public static final Tier STEEL = registerToolMaterial(3200, 6.0f, 3.0f, KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL, 0, Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS));
    public static final Tier STEEL_PLATE = registerToolMaterial(3200, 6.0f, 3.0f, KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL, 0, Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES));
    public static final Tier STEEL_NUGGET = registerToolMaterial(640, 6.0f, 3.0f, KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL, 0, Ingredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS));

    private static Tier registerToolMaterial(
            int durability,
            float miningSpeedMultiplier,
            float attackDamage,
            TagKey<Block> inverseTag,
            int enchantability,
            Ingredient repairIngredient) {
        return new Tier() {
            @Override
            public int getUses() {
                return durability;
            }

            @Override
            public float getSpeed() {
                return miningSpeedMultiplier;
            }

            @Override
            public float getAttackDamageBonus() {
                return attackDamage;
            }

            @Override
            public TagKey<Block> getIncorrectBlocksForDrops() {
                return inverseTag;
            }

            @Override
            public int getEnchantmentValue() {
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
