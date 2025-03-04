package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonItemTags {
    // makeshift tags
    public static final TagKey<Item> MAKESHIFT_CRAFTING_INGREDIENTS =
            createTag("makeshift_crafting_ingredients");
    public static final TagKey<Item> MAKESHIFT_REPAIR_MATERIALS =
            createTag("makeshift_repair_materials");

    // makeshift crafting tags
    public static final TagKey<Item> CRUDE_INCLUSIVE_STEEL_INGOTS =
            createTag("crude_inclusive_steel_ingots");
    public static final TagKey<Item> CRUDE_INCLUSIVE_STEEL_NUGGETS =
            createTag("crude_inclusive_steel_nuggets");
    public static final TagKey<Item> CRUDE_INCLUSIVE_STEEL_PLATES =
            createTag("crude_inclusive_steel_plates");
    public static final TagKey<Item> CRUDE_INCLUSIVE_STEEL_BLOCKS =
            createTag("crude_inclusive_steel_blocks");

    // anvil related tags
    public static final TagKey<Item> INFINITELY_REPAIRABLE =
            createTag("infinitely_repairable");
    public static final TagKey<Item> NO_XP_COST_REPAIRABLE =
            createTag("no_xp_cost_repairable");
    public static final TagKey<Item> UNENCHANTABLE =
            createTag("unenchantable");
    public static final TagKey<Item> STEEL_REPAIRABLE_FLINT_AND_STEEL =
            createTag("steel_repairable_flint_and_steel");

    // gear modifier tags
    public static final TagKey<Item> HEAVY_EQUIPMENT =
            createTag("heavy_equipment");
    public static final TagKey<Item> FERROMAGNETIC_ITEMS =
            createTag("ferromagnetic_items");
    public static final TagKey<Item> FERROMAGNETIC_ITEM_BLACKLIST =
            createTag("ferromagnetic_item_blacklist");

    // gear categories
    public static final TagKey<Item> STEEL_ARMOR =
            createTag("steel_armor");

    // advancement-related tags
    public static final TagKey<Item> KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS =
            createTag("root_advancement_granting_items");
    public static final TagKey<Item> MAKESHIFT_CRAFTED_EQUIPMENT =
            createTag("makeshift_crafted_equipment");

    private static TagKey<Item> createTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, KlaxonCommon.locate(name));
    }
}
