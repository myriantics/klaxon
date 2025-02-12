package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonItemTags {
    // klaxon's tags
    public static final TagKey<Item> MAKESHIFT_CRAFTING_INGREDIENTS =
            createTag("makeshift_crafting_ingredients");
    public static final TagKey<Item> MAKESHIFT_REPAIR_MATERIALS =
            createTag("makeshift_repair_materials");
    public static final TagKey<Item> INFINITELY_REPAIRABLE =
            createTag("infinitely_repairable");
    public static final TagKey<Item> NO_XP_COST_REPAIRABLE =
            createTag("no_xp_cost_repairable");
    public static final TagKey<Item> HEAVY_EQUIPMENT =
            createTag("heavy_equipment");
    public static final TagKey<Item> MAGNETIZABLE_EQUIPMENT =
            createTag("magnetizable_equipment");
    public static final TagKey<Item> STEEL_ARMOR =
            createTag("steel_armor");

    public static final TagKey<Item> CRUDE_INCLUSIVE_STEEL_INGOTS =
            createTag("crude_inclusive_steel_ingots");
    public static final TagKey<Item> CRUDE_INCLUSIVE_STEEL_NUGGETS =
            createTag("crude_inclusive_steel_nuggets");
    public static final TagKey<Item> CRUDE_INCLUSIVE_STEEL_PLATES =
            createTag("crude_inclusive_steel_plates");
    public static final TagKey<Item> CRUDE_INCLUSIVE_STEEL_BLOCKS =
            createTag("crude_inclusive_steel_blocks");

    private static TagKey<Item> createTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, KlaxonCommon.locate(name));
    }
}
