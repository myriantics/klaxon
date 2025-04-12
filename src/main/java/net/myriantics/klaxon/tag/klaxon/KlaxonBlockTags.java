package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonBlockTags {

    // mining-related tags
    public static final TagKey<Block> HAMMER_MINEABLE =
            createMineableTag("hammer");
    public static final TagKey<Block> HAMMER_INSTABREAKABLE =
            createInstabreakableTag("hammer");
    public static final TagKey<Block> CABLE_SHEARS_MINEABLE =
            createMineableTag("cable_shears");
    public static final TagKey<Block> CABLE_SHEARS_INSTABREAKABLE =
            createInstabreakableTag("cable_shears");
    public static final TagKey<Block> CLEAVER_MINEABLE =
            createMineableTag("cleaver");
    public static final TagKey<Block> CLEAVER_INSTABREAKABLE =
            createInstabreakableTag("cleaver");
    public static final TagKey<Block> WRENCH_MINEABLE =
            createMineableTag("wrench");
    public static final TagKey<Block> WRENCH_INSTABREAKABLE =
            createInstabreakableTag("wrench");
    public static final TagKey<Block> INCORRECT_FOR_STEEL_TOOL =
            createTag("incorrect_for_steel_tool");

    // behavior tags
    public static final TagKey<Block> WRENCHABLE_BLOCKS =
            createTag("wrenchable_blocks");
    public static final TagKey<Block> FERROMAGNETIC_BLOCKS =
            createTag("ferromagnetic_blocks");
    public static final TagKey<Block> COLD_BLOCKS =
            createTag("cold_blocks");

    // advancement tags
    public static final TagKey<Block> BLAST_PROCESSORS =
            createTag("blast_processors");

    private static TagKey<Block> createInstabreakableTag(String name) {
        return createTag("instabreakable/" + name);
    }

    private static TagKey<Block> createMineableTag(String name) {
        return createTag("mineable/" + name);
    }

    private static TagKey<Block> createTag(String name) {
        return TagKey.of(RegistryKeys.BLOCK, KlaxonCommon.locate(name));
    }
}
