package net.myriantics.klaxon.tag.convention;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
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
    public static final TagKey<Item> IRON_PLATES = createConventionalPlateTag("iron");
    public static final TagKey<Item> GOLD_PLATES = createConventionalPlateTag("gold");
    public static final TagKey<Item> COPPER_PLATES = createConventionalPlateTag("copper");

    public static final TagKey<Item> UNENCHANTABLE = createConventionalItemTag("unenchantable");

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
