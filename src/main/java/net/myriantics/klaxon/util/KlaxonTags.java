package net.myriantics.klaxon.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonMain;

public class KlaxonTags {
    public static class Items {
        public static final TagKey<Item> BLAST_CHAMBER_FUEL_REGULAR =
                createTag("blast_chamber_fuel_regular");
        public static final TagKey<Item> BLAST_CHAMBER_FUEL_SUPER =
                createTag("blast_chamber_fuel_super");
        public static final TagKey<Item> BLAST_CHAMBER_FUEL_HYPER =
                createTag("blast_chamber_fuel_hyper");

        public static final TagKey<Item> STEEL_INGOTS =
                TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/steel"));

        public static final TagKey<Item> SHEILD_DISABLING_MELEE =
                createTag("shield_disabling_melee");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(KlaxonMain.MOD_ID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> HAMMER_INTERACTION_POINT =
                createTag("hammer_interaction_base");
        public static final TagKey<Block> HAMMER_MINEABLE =
                createTag("hammer_mineable");

        public static final TagKey<Block> GLASS_BLOCKS =
                TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "glass_blocks"));
        public static final TagKey<Block> GLASS_PANES =
                TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "glass_panes"));

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(KlaxonMain.MOD_ID, name));
        }
    }

    public static class DamageTypeTags {
        public static final TagKey<DamageType> SHIELD_PUNCTURE_DAMAGE_TYPES =
                createTag("shield_puncture_damage_types");


        private static TagKey<DamageType> createTag(String name) {
            return TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(KlaxonMain.MOD_ID, name));
        }
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> HEAVY_HITTERS =
                createTag("heavy_hitter_entities");


        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(KlaxonMain.MOD_ID, name));
        }
    }
}
