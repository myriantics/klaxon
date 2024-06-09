package net.myriantics.klaxon.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonMain;

public class KlaxonTags {
    public static class Items {
        public static final TagKey<Item> EXTRAPOLATOR_FUEL_REGULAR =
                createTag("extrapolator_fuel_regular");
        public static final TagKey<Item> EXTRAPOLATOR_FUEL_SUPER =
                createTag("extrapolator_fuel_super");
        public static final TagKey<Item> EXTRAPOLATOR_FUEL_HYPER =
                createTag("extrapolator_fuel_hyper");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(KlaxonMain.MOD_ID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> HAMMER_INTERACTION_POINT =
                createTag("hammer_interaction_base");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(KlaxonMain.MOD_ID, name));
        }
    }
}
