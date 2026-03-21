package net.myriantics.klaxon.tag.convention;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class KlaxonConventionalItemTags {

    // categories
    public static final TagKey<Item> WIRES = createConventionalItemTag("wires");
    public static final TagKey<Item> PLATES = createConventionalItemTag("plates");
    public static final TagKey<Item> SHEETS = createConventionalItemTag("sheets");
    public static final TagKey<Item> GLOBS = createConventionalItemTag("globs");

    // logs
    public static final TagKey<Item> NATURAL_LOGS = createConventionalItemTag("natural_logs");
    public static final TagKey<Item> NATURAL_WOODS = createConventionalItemTag("natural_woods");

    // nuggies
    public static final TagKey<Item> STEEL_NUGGETS = createConventionalNuggetTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_NUGGETS = createConventionalNuggetTag("crude_steel");
    public static final TagKey<Item> COPPER_NUGGETS = createConventionalNuggetTag("copper");

    // storage blocks
    public static final TagKey<Item> STEEL_BLOCKS = createConventionalStorageBlockTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_BLOCKS = createConventionalStorageBlockTag("crude_steel");
    public static final TagKey<Item> RUBBER_BLOCKS = createConventionalStorageBlockTag("rubber");
    public static final TagKey<Item> MOLTEN_RUBBER_BLOCKS = createConventionalStorageBlockTag("molten_rubber");

    // materials
    public static final TagKey<Item> RUBBER_GLOBS = createConventionalGlobTag("rubber");

    // ingots
    public static final TagKey<Item> STEEL_INGOTS = createConventionalIngotTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_INGOTS = createConventionalIngotTag("crude_steel");

    // plates
    public static final TagKey<Item> STEEL_PLATES = createConventionalPlateTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_PLATES = createConventionalPlateTag("crude_steel");
    public static final TagKey<Item> IRON_PLATES = createConventionalPlateTag("iron");
    public static final TagKey<Item> GOLD_PLATES = createConventionalPlateTag("gold");
    public static final TagKey<Item> COPPER_PLATES = createConventionalPlateTag("copper");

    // wires
    public static final TagKey<Item> STEEL_WIRES = createConventionalWireTag("steel");
    public static final TagKey<Item> IRON_WIRES = createConventionalWireTag("iron");
    public static final TagKey<Item> GOLD_WIRES = createConventionalWireTag("gold");
    public static final TagKey<Item> COPPER_WIRES = createConventionalWireTag("copper");

    // sheets
    public static final TagKey<Item> RUBBER_SHEETS = createConventionalSheetTag("rubber");

    // tools
    public static final TagKey<Item> CLEAVER = createConventionalToolTag("cleaver");
    public static final TagKey<Item> CLEAVERS = createConventionalToolTag("cleavers");
    public static final TagKey<Item> KNIFE = createConventionalToolTag("knife");
    public static final TagKey<Item> KNIVES = createConventionalToolTag("knives");
    public static final TagKey<Item> HAMMER = createConventionalToolTag("hammer");
    public static final TagKey<Item> HAMMERS = createConventionalToolTag("hammers");
    public static final TagKey<Item> WRENCH = createConventionalToolTag("wrench");
    public static final TagKey<Item> WRENCHES = createConventionalToolTag("wrenches");

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

    private static TagKey<Item> createConventionalWireTag(String name) {
        return createConventionalItemTag("wires/" + name);
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
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
    }
}
