package net.myriantics.klaxon.tag.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class KlaxonCompatBlockTags {

    public static String SUPPLEMENTARIES_MODID = "supplementaries";
    public static String CREATE_MODID = "create";

    public static TagKey<Block> BRICK_BREAKABLE = createSupplementariesCompatBlockTag("brick_breakable");
    public static TagKey<Block> IGNITE_FLINT_BLOCKS = createSupplementariesCompatBlockTag("ignite_flint_blocks");
    public static TagKey<Block> WRENCH_PICKUP = createCreateModCompatBlockTag("wrench_pickup");

    private static TagKey<Block> createSupplementariesCompatBlockTag(String name) {
        return createCompatBlockTag(SUPPLEMENTARIES_MODID, name);
    }

    private static TagKey<Block> createCreateModCompatBlockTag(String name) {
        return createCompatBlockTag(CREATE_MODID, name);
    }

    private static TagKey<Block> createCompatBlockTag(String namespace, String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, name));
    }
}
