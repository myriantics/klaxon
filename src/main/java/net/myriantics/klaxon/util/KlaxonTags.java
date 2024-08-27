package net.myriantics.klaxon.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonTags {
    public static class Items {
        public static final TagKey<Item> STEEL_INGOTS =
                TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/steel"));

        public static final TagKey<Item> STEEL_NUGGETS =
                TagKey.of(RegistryKeys.ITEM, new Identifier("c", "nuggets/steel"));

        public static final TagKey<Item> STEEL_BLOCKS =
                TagKey.of(RegistryKeys.ITEM, new Identifier("c", "storage_blocks/steel"));
        public static final TagKey<Item> SHIELD_DISABLING_MELEE_WEAPONS =
                createTag("shield_disabling_melee_weapons");


        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(KlaxonCommon.MOD_ID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> HAMMER_INTERACTION_POINT =
                createTag("hammer_interaction_base");
        public static final TagKey<Block> HAMMER_MINEABLE =
                createTag("mineable/hammer");
        public static final TagKey<Block> HAMMER_INSTABREAK =
                createTag("hammer_instabreak");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(KlaxonCommon.MOD_ID, name));
        }
    }

    public static class DamageTypeTags {

        private static TagKey<DamageType> createTag(String name) {
            return TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(KlaxonCommon.MOD_ID, name));
        }
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> HEAVY_HITTERS =
                createTag("heavy_hitter_entities");
        public static final TagKey<EntityType<?>> BOUNCY_ENTITIES =
                createTag("bouncy_entities");


        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(KlaxonCommon.MOD_ID, name));
        }
    }
}
