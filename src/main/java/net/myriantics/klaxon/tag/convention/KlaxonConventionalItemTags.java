package net.myriantics.klaxon.tag.convention;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class KlaxonConventionalItemTags {
    // convention tags
    public static final TagKey<Item> STEEL_INGOTS = createConventionalIngotTag("steel");
    public static final TagKey<Item> STEEL_NUGGETS = createConventionalNuggetTag("steel");
    public static final TagKey<Item> STEEL_BLOCKS = createConventionalStorageBlockTag("steel");
    public static final TagKey<Item> STEEL_PLATES = createConventionalPlateTag("steel");

    private static TagKey<Item> createConventionalNuggetTag(String name) {
        return createConventionalItemTag("nuggets/" + name);
    }

    private static TagKey<Item> createConventionalIngotTag(String name) {
        return createConventionalItemTag("ingots/" + name);
    }

    private static TagKey<Item> createConventionalPlateTag(String name) {
        return createConventionalItemTag("plates/" + name);
    }

    private static TagKey<Item> createConventionalStorageBlockTag(String name) {
        return createConventionalItemTag("storage_blocks/" + name);
    }

    private static TagKey<Item> createConventionalItemTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of("c", name));
    }
}
