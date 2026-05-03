package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonItemTags {
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
    public static final TagKey<Item> CRUDE_INCLUSIVE_STEEL_CASING =
            createTag("crude_inclusive_steel_casings");
    public static final TagKey<Item> MOLTEN_INCLUSIVE_RUBBER_BLOCKS =
            createTag("molten_inclusive_rubber_blocks");

    // crafting tags
    public static final TagKey<Item> LOW_YIELD_RUBBER_EXTRACTABLE_LOGS =
            createTag("low_yield_rubber_extractable_logs");
    public static final TagKey<Item> HIGH_YIELD_RUBBER_EXTRACTABLE_LOGS =
            createTag("high_yield_rubber_extractable_logs");
    public static final TagKey<Item> GEAR_GRIP_MATERIALS =
            createTag("gear_grip_materials");
    public static final TagKey<Item> STEEL_NUGGET_COOKING_RECYCLABLES =
            createTag("steel_nugget_cooking_recyclables");
    public static final TagKey<Item> STEEL_INGOT_COOKING_RECYCLABLES =
            createTag("steel_ingot_cooking_recyclables");
    public static final TagKey<Item> SUSPICIOUS_STEW_INGREDIENTS =
            createTag("suspicious_stew_ingredients");

    // anvil related tags
    public static final TagKey<Item> INFINITELY_REPAIRABLE =
            createTag("infinitely_repairable");
    public static final TagKey<Item> NO_XP_COST_REPAIRABLE =
            createTag("no_xp_cost_repairable");
    public static final TagKey<Item> UNENCHANTABLE =
            createTag("unenchantable");

    // repair tags
    public static final TagKey<Item> STEEL_INGOT_TOOL_MATERIAL_REPAIR_MATERIALS =
            createRepairTag("tool_material/steel_ingot");
    public static final TagKey<Item> STEEL_PLATE_TOOL_MATERIAL_REPAIR_MATERIALS =
            createRepairTag("tool_material/steel_plate");
    public static final TagKey<Item> STEEL_NUGGET_TOOL_MATERIAL_REPAIR_MATERIALS =
            createRepairTag("tool_material/steel_nugget");
    public static final TagKey<Item> STEEL_PLATE_ARMOR_MATERIAL_REPAIR_MATERIALS =
            createRepairTag("armor_material/steel_plate");
    public static final TagKey<Item> LIGHTER_REPAIR_MATERIALS =
            createRepairTag("lighter");

    // behavior tags
    public static final TagKey<Item> WRENCHABLE_INTERFACE_TRIGGERING_TOOLS =
            createTag("wrenchable_interface_triggering_tools");
    public static final TagKey<Item> HEAVY_EQUIPMENT =
            createTag("heavy_equipment");
    public static final TagKey<Item> FERROMAGNETIC_ITEMS =
            createTag("ferromagnetic_items");
    public static final TagKey<Item> FERROMAGNETIC_ITEM_BLACKLIST =
            createTag("ferromagnetic_item_blacklist");
    public static final TagKey<Item> RECIPE_PROCESSING_HAMMERS =
            createTag("recipe_processing_hammers");
    public static final TagKey<Item> RECIPE_PROCESSING_WIRECUTTERS =
            createTag("recipe_processing_wirecutters");
    public static final TagKey<Item> EFFECTIVE_AGAINST_METAL_ENTITIES =
            createTag("effective_against_metal_entities");
    public static final TagKey<Item> GRAPPLE_CLAW_INSTAKILL =
            createTag("grapple_claw_instakill");
    public static final TagKey<Item> GRAPPLE_WINCH_CABLE_DETACHERS =
            createTag("grapple_winch_cable_detachers");
    public static final TagKey<Item> PICK_BLOCK_SLOT_REPLACEMENT_DISCOURAGED =
            createTag("pick_block_slot_replacement_discouraged");
    public static final TagKey<Item> DEFUSERS =
            createTag("defusers");
    public static final TagKey<Item> MUFFLERS =
            createTag("mufflers");
    public static final TagKey<Item> MUFFLER_REMOVERS =
            createTag("muffler_removers");

    // enchantment tags
    public static final TagKey<Item> STREAMLINE_ENCHANTABLE =
            createEnchantableTag("streamline");

    // gear categories
    public static final TagKey<Item> STEEL_ARMOR =
            createTag("steel_armor");
    public static final TagKey<Item> STEEL_EQUIPMENT =
            createTag("steel_equipment");
    public static final TagKey<Item> CABLE_SHEARS =
            createToolTag("cable_shears");

    // advancement-related tags
    public static final TagKey<Item> KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS =
            createTag("root_advancement_granting_items");
    public static final TagKey<Item> MAKESHIFT_CRAFTED_EQUIPMENT =
            createTag("makeshift_crafted_equipment");
    public static final TagKey<Item> EPIC_MUFFLERS =
            createTag("epic_mufflers");


    // blast processor behavior tags
    public static final TagKey<Item> BEDLIKE_EXPLODABLES =
            createTag("bedlike_explodables");

    // category tags
    public static final TagKey<Item> FRACTURED_MATERIALS = createTag("fractured_materials");
    public static final TagKey<Item> GRAPPLE_CLAWS = createTag("grapple_claws");

    // fractured materials
    public static final TagKey<Item> FRACTURED_COALS = createFracturedMaterialTag("coal");
    public static final TagKey<Item> FRACTURED_IRON = createFracturedMaterialTag("iron");
    public static final TagKey<Item> FRACTURED_GOLD = createFracturedMaterialTag("gold");
    public static final TagKey<Item> FRACTURED_COPPER = createFracturedMaterialTag("copper");
    public static final TagKey<Item> FRACTURED_RAW_IRON = createFracturedMaterialTag("raw_iron");
    public static final TagKey<Item> FRACTURED_RAW_GOLD = createFracturedMaterialTag("raw_gold");
    public static final TagKey<Item> FRACTURED_RAW_COPPER = createFracturedMaterialTag("raw_copper");

    // wood tags
    public static final TagKey<Item> HALLNOX_STEMS = createTag("hallnox_stems");

    private static TagKey<Item> createFracturedMaterialTag(String name) {
        return createTag("fractured_materials/" + name);
    }

    private static TagKey<Item> createToolTag(String name) {
        return createTag("tools/" + name);
    }


    private static TagKey<Item> createRepairTag(String name) {
        return createTag("repair/" + name);
    }

    private static TagKey<Item> createEnchantableTag(String name) {
        return createTag("enchantable/" + name);
    }

    private static TagKey<Item> createTag(String name) {
        return TagKey.create(Registries.ITEM, KlaxonCommon.locate(name));
    }
}
