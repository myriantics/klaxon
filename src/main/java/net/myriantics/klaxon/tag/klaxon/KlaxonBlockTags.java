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
    public static final TagKey<Block> MACHINE_MUFFLING_BLOCKS =
            createTag("machine_muffling_blocks");
    public static final TagKey<Block> FERROMAGNETIC_BLOCKS =
            createTag("ferromagnetic_blocks");

    private static TagKey<Block> createTag(String name) {
        return TagKey.of(RegistryKeys.BLOCK, KlaxonCommon.locate(name));
    }
}
