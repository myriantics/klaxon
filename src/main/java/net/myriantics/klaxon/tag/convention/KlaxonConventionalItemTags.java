package net.myriantics.klaxon.tag.convention;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class KlaxonConventionalItemTags {

    // categories
    public static final TagKey<Item> PLATES = createConventionalItemTag("plates");
    public static final TagKey<Item> SHEETS = createConventionalItemTag("sheets");
    public static final TagKey<Item> GLOBS = createConventionalItemTag("globs");

    // nuggies
    public static final TagKey<Item> STEEL_NUGGETS = createConventionalNuggetTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_NUGGETS = createConventionalNuggetTag("crude_steel");

    // storage blocks
    public static final TagKey<Item> STEEL_BLOCKS = createConventionalStorageBlockTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_BLOCKS = createConventionalStorageBlockTag("crude_steel");
    public static final TagKey<Item> RUBBER_BLOCKS = createConventionalStorageBlockTag("rubber");
    public static final TagKey<Item> MOLTEN_RUBBER_BLOCKS = createConventionalStorageBlockTag("molten_rubber");

    // materials
    public static final TagKey<Item> RUBBER_GLOBS = createConventionalGlobTag("rubber");
    public static final TagKey<Item> MOLTEN_RUBBER_GLOBS = createConventionalGlobTag("molten_rubber");

    // ingots
    public static final TagKey<Item> STEEL_INGOTS = createConventionalIngotTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_INGOTS = createConventionalIngotTag("crude_steel");

    // plates
    public static final TagKey<Item> STEEL_PLATES = createConventionalPlateTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_PLATES = createConventionalPlateTag("crude_steel");
    public static final TagKey<Item> IRON_PLATES = createConventionalPlateTag("iron");
    public static final TagKey<Item> GOLD_PLATES = createConventionalPlateTag("gold");
    public static final TagKey<Item> COPPER_PLATES = createConventionalPlateTag("copper");

    // sheets
    public static final TagKey<Item> RUBBER_SHEETS = createConventionalSheetTag("rubber");
    public static final TagKey<Item> MOLTEN_RUBBER_SHEETS = createConventionalSheetTag("molten_rubber");

    // tools
    public static final TagKey<Item> CLEAVERS = createConventionalToolTag("cleavers");
    public static final TagKey<Item> KNIVES = createConventionalToolTag("knives");
    public static final TagKey<Item> HAMMERS = createConventionalToolTag("hammers");
    public static final TagKey<Item> WRENCHES = createConventionalToolTag("wrenches");
    public static final TagKey<Item> SHEARS = createConventionalToolTag("shears");

    private static TagKey<Item> createConventionalNuggetTag(String name) {
        return createConventionalItemTag("nuggets/" + name);
    }

    private static TagKey<Item> createConventionalIngotTag(String name) {
        return createConventionalItemTag("ingots/" + name);
    }

    private static TagKey<Item> createConventionalStorageBlockTag(String name) {
        return createConventionalItemTag("storage_blocks/" + name);
    }

    private static TagKey<Item> createConventionalPlateTag(String name) {
        return createConventionalItemTag("plates/" + name);
    }

    private static TagKey<Item> createConventionalSheetTag(String name) {
        return createConventionalItemTag("sheets/" + name);
    }

    private static TagKey<Item> createConventionalGlobTag(String name) {
        return createConventionalItemTag("globs/" + name);
    }

    private static TagKey<Item> createConventionalToolTag(String name) {
        return createConventionalItemTag("tools/" + name);
    }

    private static TagKey<Item> createConventionalItemTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of("c", name));
    }
}
