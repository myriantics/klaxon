package net.myriantics.klaxon.tag.convention;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class KlaxonConventionalItemTags {

    // categories
    public static final TagKey<Item> PLATES = createConventionalItemTag("plates");
    public static final TagKey<Item> STORAGE_BLOCKS = createConventionalItemTag("storage_blocks");
    public static final TagKey<Item> MINING_TOOL_TOOLS = createConventionalItemTag("mining_tool_tools");
    public static final TagKey<Item> MELEE_WEAPON_TOOLS = createConventionalItemTag("melee_weapon_tools");
    public static final TagKey<Item> ARMORS = createConventionalItemTag("armors");
    public static final TagKey<Item> HEAD_ARMOR = createConventionalItemTag("head_armor");
    public static final TagKey<Item> CHEST_ARMOR = createConventionalItemTag("chest_armor");
    public static final TagKey<Item> LEG_ARMOR = createConventionalItemTag("leg_armor");
    public static final TagKey<Item> FOOT_ARMOR = createConventionalItemTag("foot_armor");

    // nuggies
    public static final TagKey<Item> STEEL_NUGGETS = createConventionalNuggetTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_NUGGETS = createConventionalNuggetTag("crude_steel");

    // storage blocks
    public static final TagKey<Item> STEEL_BLOCKS = createConventionalStorageBlockTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_BLOCKS = createConventionalStorageBlockTag("crude_steel");

    // ingots
    public static final TagKey<Item> STEEL_INGOTS = createConventionalIngotTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_INGOTS = createConventionalIngotTag("crude_steel");

    // plates
    public static final TagKey<Item> STEEL_PLATES = createConventionalPlateTag("steel");
    public static final TagKey<Item> CRUDE_STEEL_PLATES = createConventionalPlateTag("crude_steel");
    public static final TagKey<Item> IRON_PLATES = createConventionalPlateTag("iron");
    public static final TagKey<Item> GOLD_PLATES = createConventionalPlateTag("gold");
    public static final TagKey<Item> COPPER_PLATES = createConventionalPlateTag("copper");

    private static TagKey<Item> createConventionalNuggetTag(String name) {
        return createConventionalItemTag("nuggets/" + name);
    }

    private static TagKey<Item> createConventionalIngotTag(String name) {
        return createConventionalItemTag("ingots/" + name);
    }

    private static TagKey<Item> createConventionalStorageBlockTag(String name) {
        return createConventionalItemTag("storage_blocks/" + name);
    }

    private static TagKey<Item> createConventionalPlateStorageBlockTag(String name) {
        return createConventionalItemTag("storage_blocks/plates/" + name);
    }

    private static TagKey<Item> createConventionalPlateTag(String name) {
        return createConventionalItemTag("plates/" + name);
    }

    private static TagKey<Item> createConventionalItemTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of("c", name));
    }
}
