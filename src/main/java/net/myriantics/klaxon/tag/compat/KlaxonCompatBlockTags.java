package net.myriantics.klaxon.tag.compat;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

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
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(namespace, name));
    }
}
