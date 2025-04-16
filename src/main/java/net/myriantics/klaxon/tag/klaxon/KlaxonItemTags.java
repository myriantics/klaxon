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
    public static final TagKey<Item> MOLTEN_INCLUSIVE_RUBBER_GLOBS =
            createTag("molten_inclusive_rubber_globs");
    public static final TagKey<Item> MOLTEN_INCLUSIVE_RUBBER_SHEETS =
            createTag("molten_inclusive_rubber_sheets");
    public static final TagKey<Item> MOLTEN_INCLUSIVE_RUBBER_BLOCKS =
            createTag("molten_inclusive_rubber_blocks");

    // crafting tags
    public static final TagKey<Item> OVERWORLD_RUBBER_EXTRACTABLE_LOGS =
            createTag("overworld_rubber_extractable_logs");
    public static final TagKey<Item> NETHER_RUBBER_EXTRACTABLE_LOGS =
            createTag("nether_rubber_extractable_logs");
    public static final TagKey<Item> GEAR_GRIP_MATERIALS =
            createTag("gear_grip_materials");

    // anvil related tags
    public static final TagKey<Item> INFINITELY_REPAIRABLE =
            createTag("infinitely_repairable");
    public static final TagKey<Item> NO_XP_COST_REPAIRABLE =
            createTag("no_xp_cost_repairable");
    public static final TagKey<Item> UNENCHANTABLE =
            createTag("unenchantable");
    public static final TagKey<Item> STEEL_REPAIRABLE_FLINT_AND_STEEL =
            createTag("steel_repairable_flint_and_steel");
    public static final TagKey<Item> INNATE_UNBREAKING_EQUIPMENT =
            // used in EnchantmentHelper
            createTag("innate_unbreaking_equipment");

    // behavior tags
    public static final TagKey<Item> HEAVY_EQUIPMENT =
            createTag("heavy_equipment");
    public static final TagKey<Item> FERROMAGNETIC_ITEMS =
            createTag("ferromagnetic_items");
    public static final TagKey<Item> FERROMAGNETIC_ITEM_BLACKLIST =
            createTag("ferromagnetic_item_blacklist");
    public static final TagKey<Item> WRENCHABLE_BLOCK_WHITELIST =
            createTag("wrenchable_block_blacklist");
    public static final TagKey<Item> WRENCHABLE_BLOCK_BLACKLIST =
            createTag("wrenchable_block_blacklist");

    // gear categories
    public static final TagKey<Item> STEEL_ARMOR =
            createTag("steel_armor");
    public static final TagKey<Item> STEEL_EQUIPMENT =
            createTag("steel_equipment");

    // advancement-related tags
    public static final TagKey<Item> KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS =
            createTag("root_advancement_granting_items");
    public static final TagKey<Item> MAKESHIFT_CRAFTED_EQUIPMENT =
            createTag("makeshift_crafted_equipment");

    // blast processor behavior tags
    public static final TagKey<Item> BEDLIKE_EXPLODABLES =
            createTag("bedlike_explodables");

    private static TagKey<Item> createTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, KlaxonCommon.locate(name));
    }
}
