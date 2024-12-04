package net.myriantics.klaxon.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

import javax.swing.text.html.HTML;

public class KlaxonTags {
    public static class Items {

        // klaxon's tags
        public static final TagKey<Item> SHIELD_DISABLING_MELEE_WEAPONS =
                createTag("shield_disabling_melee_weapons");
        public static final TagKey<Item> ITEM_EXPLOSION_POWER_EMI_OMITTED =
                createTag("item_explosion_power_emi_omitted");

        // convention tags
        public static final TagKey<Item> STEEL_INGOTS =
                TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "ingots/steel"));

        public static final TagKey<Item> STEEL_NUGGETS =
                TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "nuggets/steel"));

        public static final TagKey<Item> STEEL_BLOCKS =
                TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "storage_blocks/steel"));

        public static final TagKey<Item> COAL =
                TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "coal"));


        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, KlaxonCommon.locate(name));
        }
    }

    public static class Blocks {
        // klaxon's tags
        public static final TagKey<Block> HAMMER_INTERACTION_POINT =
                createTag("hammer_interaction_base");
        public static final TagKey<Block> HAMMER_MINEABLE =
                createTag("mineable/hammer");
        public static final TagKey<Block> HAMMER_INSTABREAK =
                createTag("hammer_instabreak");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, KlaxonCommon.locate(name));
        }
    }

    public static class DamageTypeTags {

        private static TagKey<DamageType> createTag(String name) {
            return TagKey.of(RegistryKeys.DAMAGE_TYPE, KlaxonCommon.locate(name));
        }
    }

    public static class Entities {
        // klaxon's tags
        public static final TagKey<EntityType<?>> HEAVY_HITTERS =
                createTag("heavy_hitter_entities");


        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, KlaxonCommon.locate(name));
        }
    }
}
