package net.myriantics.klaxon.tag.convention;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class KlaxonConventionalBlockTags {

    // category tags
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

    public static final TagKey<Block> NATURAL_LOGS =
            createConventionalBlockTag("natural_logs");
    public static final TagKey<Block> NATURAL_WOODS =
            createConventionalBlockTag("natural_woods");

    private static TagKey<Block> createConventionalStorageBlockTag(String name) {
        return createConventionalBlockTag("storage_blocks/" + name);
    }

    private static TagKey<Block> createConventionalBlockTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", name));
    }
}
