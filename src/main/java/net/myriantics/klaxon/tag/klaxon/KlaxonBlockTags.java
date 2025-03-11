package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonBlockTags {
    // klaxon's tags
    public static final TagKey<Block> HAMMER_MINEABLE =
            createTag("mineable/hammer");
    public static final TagKey<Block> HAMMER_INSTABREAKABLE =
            createTag("hammer_instabreakable");
    public static final TagKey<Block> INCORRECT_FOR_STEEL_TOOL =
            createTag("incorrect_for_steel_tool");
    public static final TagKey<Block> FERROMAGNETIC_BLOCKS =
            createTag("ferromagnetic_blocks");
    public static final TagKey<Block> BLAST_PROCESSORS =
            createTag("blast_processors");

    private static TagKey<Block> createTag(String name) {
        return TagKey.of(RegistryKeys.BLOCK, KlaxonCommon.locate(name));
    }
}
