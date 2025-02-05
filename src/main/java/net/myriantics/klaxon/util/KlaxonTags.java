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

        // klaxon's tags
        public static final TagKey<Item> MAKESHIFT_CRAFTING_INGREDIENTS =
                createTag("makeshift_crafting_ingredients");
        public static final TagKey<Item> MAKESHIFT_REPAIR_MATERIALS =
                createTag("makeshift_repair_materials");
        public static final TagKey<Item> INFINITELY_REPAIRABLE =
                createTag("infinitely_repairable");
        public static final TagKey<Item> NO_XP_COST_REPAIRABLE =
                createTag("no_xp_cost_repairable");

        // convention tags
        public static final TagKey<Item> STEEL_INGOTS =
                createConventionTag("ingots/steel");

        public static final TagKey<Item> STEEL_NUGGETS =
                createConventionTag("nuggets/steel");

        public static final TagKey<Item> STEEL_BLOCKS =
                createConventionTag("storage_blocks/steel");

        public static final TagKey<Item> COAL =
                createConventionTag("coal");


        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, KlaxonCommon.locate(name));
        }

        private static TagKey<Item> createConventionTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of("c", name));
        }
    }

    public static class Blocks {
        // klaxon's tags
        public static final TagKey<Block> HAMMER_MINEABLE =
                createTag("mineable/hammer");
        public static final TagKey<Block> HAMMER_INSTABREAKABLE =
                createTag("hammer_instabreakable");
        public static final TagKey<Block> MACHINE_MUFFLING_BLOCKS =
                createTag("machine_muffling_blocks");

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
