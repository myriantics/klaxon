package net.myriantics.klaxon.tag.convention;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public class KlaxonConventionalBlockTags {

    // category tags
    public static final TagKey<Block> STEEL =
            createConventionalBlockTag("steel");
    public static final TagKey<Block> CRUDE_STEEL =
            createConventionalBlockTag("crude_steel");
    public static final TagKey<Block> RUBBER =
            createConventionalBlockTag("rubber");
    public static final TagKey<Block> SCULK =
            createConventionalBlockTag("sculk");
    public static final TagKey<Block> VINES =
            createConventionalBlockTag("vines");


    public static final TagKey<Block> STEEL_STORAGE_BLOCKS =
            createConventionalStorageBlockTag("steel");
    public static final TagKey<Block> CRUDE_STEEL_STORAGE_BLOCKS =
            createConventionalStorageBlockTag("crude_steel");
    public static final TagKey<Block> RUBBER_STORAGE_BLOCKS =
            createConventionalStorageBlockTag("rubber");

    public static final TagKey<Block> METAL_DOORS =
            createConventionalBlockTag("metal_doors");
    public static final TagKey<Block> METAL_TRAPDOORS =
            createConventionalBlockTag("metal_trapdoors");

    public static final TagKey<Block> SCAFFOLDINGS =
            createConventionalBlockTag("scaffoldings");
    public static final TagKey<Block> GRATES =
            createConventionalBlockTag("grates");
    public static final TagKey<Block> LEVERS =
            createConventionalBlockTag("levers");

    private static TagKey<Block> createConventionalStorageBlockTag(String name) {
        return createConventionalBlockTag("storage_blocks/" + name);
    }

    private static TagKey<Block> createConventionalBlockTag(String name) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of("c", name));
    }
}
