package net.myriantics.klaxon.tag.compat;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class KlaxonCompatItemTags {

    // modIds
    public static String COMBAT_AMENITIES_MOD_ID = "combatamenities";

    // Combat Amenities
    public static TagKey<Item> COMBAT_AMENITIES_KEEP_DURABILITY = createCompatItemTag(COMBAT_AMENITIES_MOD_ID, "keep_durability");

    private static TagKey<Item> createCompatItemTag(String namespace, String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(namespace, name));
    }
}
